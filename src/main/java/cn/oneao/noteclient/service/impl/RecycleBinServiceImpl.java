package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.enums.NoteActionEnums;
import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.mapper.NoteMapper;
import cn.oneao.noteclient.mapper.RecycleBinMapper;
import cn.oneao.noteclient.mapper.SmallNoteMapper;
import cn.oneao.noteclient.pojo.dto.RecycleBinDTO;
import cn.oneao.noteclient.pojo.dto.RecycleBinManyDTO;
import cn.oneao.noteclient.pojo.dto.RecycleBinRecoverDTO;
import cn.oneao.noteclient.pojo.entity.RecycleBin;
import cn.oneao.noteclient.pojo.entity.es.ESNote;
import cn.oneao.noteclient.pojo.entity.log.NoteLog;
import cn.oneao.noteclient.pojo.entity.note.Note;
import cn.oneao.noteclient.pojo.vo.RecycleBinVO;
import cn.oneao.noteclient.server.DirectSender;
import cn.oneao.noteclient.service.NoteLogService;
import cn.oneao.noteclient.service.RecycleBinService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import kotlin.jvm.internal.PackageReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecycleBinServiceImpl extends ServiceImpl<RecycleBinMapper, RecycleBin> implements RecycleBinService {
    @Autowired
    private RecycleBinMapper recycleBinMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private SmallNoteMapper smallNoteMapper;
    @Autowired
    private NoteLogService noteLogService;
    @Autowired
    private DirectSender directSender;

    @Override
    public Result<Object> getRecycleBin(RecycleBinDTO recycleBinDTO) {
        int userId = UserContext.getUserId();
        if (ObjectUtils.isEmpty(userId)) {
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
        List<RecycleBin> recycleBins = recycleBinMapper.selectRecycleBin(userId, (recycleBinDTO.getPage() - 1) * recycleBinDTO.getPageSize(), recycleBinDTO.getPageSize(), recycleBinDTO.getSearchValue(), recycleBinDTO.getClassifyValue());
        Integer total = recycleBinMapper.selectTotal(userId, recycleBinDTO.getSearchValue(), recycleBinDTO.getClassifyValue());
        int serialNumber = 0;
        for (RecycleBin recycleBin : recycleBins) {
            recycleBin.setSerialNumber(++serialNumber);
        }
        RecycleBinVO recycleBinVO = new RecycleBinVO();
        //结果
        recycleBinVO.setTotal(total);
        recycleBinVO.setList(recycleBins);
        return Result.success(recycleBinVO);
    }

    @Override
    public Result<Object> recoverOneRecord(RecycleBinRecoverDTO recycleBinRecoverDTO) {
        Integer id = recycleBinRecoverDTO.getId();
        Integer type = recycleBinRecoverDTO.getType();
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(type)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        //小记
        if (type == 0) {
            smallNoteMapper.updateSmallNoteRecoverOne(id);
            noteLog.setSmallNoteId(id);
            noteLog.setAction(NoteActionEnums.USER_RECOVER_SmallNote.getActionName());
            noteLog.setActionDesc(NoteActionEnums.USER_RECOVER_SmallNote.getActionDesc());
            noteLogService.save(noteLog);
            return Result.success(ResponseEnums.SmallNote_RECOVER_SUCCESS);
        } else if (type == 1) {
            noteMapper.updateNoteRecoveryOne(id);
            //恢复添加到文档笔记中。
            Note note = noteMapper.selectById(id);
            ESNote esNote = new ESNote();
            esNote.setId(id.toString());
            esNote.setNoteId(id);
            esNote.setUserId(note.getUserId());
            esNote.setTitle(note.getNoteTitle());
            esNote.setContent(note.getNoteContent());
            esNote.setUpdateTime(note.getUpdateTime());
            directSender.sendDocumentInsertOrUpdateNotice(esNote);
            //日志
            noteLog.setNoteId(id);
            noteLog.setAction(NoteActionEnums.USER_RECOVER_Note.getActionName());
            noteLog.setActionDesc(NoteActionEnums.USER_RECOVER_Note.getActionDesc());
            noteLogService.save(noteLog);
            return Result.success(ResponseEnums.NOTE_RECOVER_SUCCESS);
        } else {
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
    }

    @Override
    public Result<Object> deleteMany(RecycleBinManyDTO recycleBinManyDTO) {
        List<Integer> types = recycleBinManyDTO.getTypes();
        List<Integer> ids = recycleBinManyDTO.getIds();
        if (ObjectUtils.isEmpty(types) || ObjectUtils.isEmpty(ids)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        List<Integer> smallNoteIds = new ArrayList<>();
        List<Integer> noteIds = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            Integer i1 = ids.get(i);//id
            Integer i2 = types.get(i);//类别
            if (i2 == 0) {
                smallNoteIds.add(i1);
            } else if (i2 == 1) {
                noteIds.add(i1);
            } else {
                return Result.error(ResponseEnums.UNKNOWN_ERROR);
            }
        }
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        //批量删除笔记
        if (!noteIds.isEmpty()) {
            noteMapper.completeDeleteNotes(noteIds);
            noteLog.setAction(NoteActionEnums.DELETE_NOTE_MANY.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_NOTE_MANY.getActionDesc() + noteIds.size() + "条笔记");
            noteLogService.save(noteLog);
        }
        //批量删除小记
        if (!smallNoteIds.isEmpty()) {
            smallNoteMapper.completeDeleteSmallNotes(smallNoteIds);
            noteLog.setAction(NoteActionEnums.DELETE_SMALL_NOTE_MANY.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_NOTE_MANY.getActionDesc() + smallNoteIds.size() + "条小记");
            noteLogService.save(noteLog);
        }
        return Result.success(ResponseEnums.MANY_DELETE_SUCCESS);
    }

    @Override
    public Result<Object> recoverMany(RecycleBinManyDTO recycleBinManyDTO) {
        List<Integer> types = recycleBinManyDTO.getTypes();
        List<Integer> ids = recycleBinManyDTO.getIds();
        if (ObjectUtils.isEmpty(types) || ObjectUtils.isEmpty(ids)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        List<Integer> smallNoteIds = new ArrayList<>();
        List<Integer> noteIds = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            Integer i1 = ids.get(i);//id
            Integer i2 = types.get(i);//类别
            if (i2 == 0) {
                smallNoteIds.add(i1);
            } else if (i2 == 1) {
                noteIds.add(i1);
            } else {
                return Result.error(ResponseEnums.UNKNOWN_ERROR);
            }
        }
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        //批量恢复笔记
        if (!noteIds.isEmpty()) {
            noteMapper.recoverMany(noteIds);
            ESNote esNote = new ESNote();
            noteIds.forEach(noteId -> {
                Note note = noteMapper.selectById(noteId);
                esNote.setId(note.getId().toString());
                esNote.setNoteId(note.getId());
                esNote.setUserId(note.getUserId());
                esNote.setTitle(note.getNoteTitle());
                esNote.setContent(note.getNoteContent());
                esNote.setUpdateTime(note.getUpdateTime());
                directSender.sendDocumentInsertOrUpdateNotice(esNote);
            });
            noteLog.setAction(NoteActionEnums.RECOVER_NOTE_MANY.getActionName());
            noteLog.setActionDesc(NoteActionEnums.RECOVER_NOTE_MANY.getActionDesc() + noteIds.size() + "条笔记");
            noteLogService.save(noteLog);
        }
        //批量恢复小记
        if (!smallNoteIds.isEmpty()) {
            smallNoteMapper.recoverMany(smallNoteIds);
            noteLog.setAction(NoteActionEnums.RECOVER_SMALL_NOTE_MANY.getActionName());
            noteLog.setActionDesc(NoteActionEnums.RECOVER_SMALL_NOTE_MANY.getActionDesc() + smallNoteIds.size() + "条小记");
            noteLogService.save(noteLog);
        }
        return Result.success(ResponseEnums.MANY_RECOVER_SUCCESS);
    }
}
