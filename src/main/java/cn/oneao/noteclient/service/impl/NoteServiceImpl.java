package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.enums.NoteActionEnums;
import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.mapper.NoteMapper;
import cn.oneao.noteclient.pojo.dto.NoteDeleteDTO;
import cn.oneao.noteclient.pojo.dto.NoteTopStatusDTO;
import cn.oneao.noteclient.pojo.dto.NoteVerifyLockPassWordDTO;
import cn.oneao.noteclient.pojo.entity.Note;
import cn.oneao.noteclient.pojo.entity.log.NoteLog;
import cn.oneao.noteclient.pojo.vo.NoteVO;
import cn.oneao.noteclient.service.NoteLogService;
import cn.oneao.noteclient.service.NoteService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private NoteLogService noteLogService;
    //获取笔记信息
    @Override
    public List<NoteVO> getNoteInfo() {
        int userId = UserContext.getUserId();
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId,userId);
        queryWrapper.orderByDesc(Note::getIsTop);
        queryWrapper.orderByDesc(Note::getUpdateTime);
        List<Note> list = this.list(queryWrapper);
        List<NoteVO> noteVOList = new ArrayList<>();
        for (Note note : list) {
            NoteVO noteVO = new NoteVO();
            BeanUtils.copyProperties(note,noteVO);
            if (noteVO.getIsLock() == 1){
                //加锁
                noteVO.setNoteBody("该笔记已加锁,暂时无法查看哦!");
                noteVO.setNoteTags(null);
            }
            if (ObjectUtils.isEmpty(noteVO.getUpdateTime())){
                noteVO.setUpdateTime(note.getCreateTime());
            }
            noteVOList.add(noteVO);
        }
        return noteVOList;
    }
    //TODO:验证锁密码是否有效
    @Override
    public Boolean verifyNoteLockPassword(NoteVerifyLockPassWordDTO noteVerifyLockPassWordDTO) {
        int userId = UserContext.getUserId();
        Integer noteId = noteVerifyLockPassWordDTO.getNoteId();
        String lockPassword = noteVerifyLockPassWordDTO.getLockPassword();
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getId,noteId).eq(Note::getUserId,userId).eq(Note::getLockPassword,lockPassword);
        Note noteOne = this.getOne(queryWrapper);
        return !ObjectUtils.isEmpty(noteOne);
    }
    //修改笔记的置顶状态
    @Override
    public void changeNoteTopStatus(NoteTopStatusDTO noteTopStatusDTO) {
        Integer noteId = noteTopStatusDTO.getNoteId();
        Integer status = noteTopStatusDTO.getStatus();
        LambdaUpdateWrapper<Note> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Note::getId,noteId);
        if (status == 1){
            //取消置顶
            updateWrapper.set(Note::getIsTop,0);
        }else if (status == 0){
            //置顶
            updateWrapper.set(Note::getIsTop,1);
        }else {
            //其他
            return;
        }
        this.update(updateWrapper);
    }
    //删除笔记
    @Override
    @Transactional
    public Result<Object> deleteNote(NoteDeleteDTO noteDeleteDTO) {
        Integer noteId = noteDeleteDTO.getNoteId();
        Integer deleteType = noteDeleteDTO.getDeleteType();
        Integer isCreateNew = noteDeleteDTO.getIsCreateNew();
        if (ObjectUtils.isEmpty(noteId) || ObjectUtils.isEmpty(deleteType) || ObjectUtils.isEmpty(isCreateNew)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        //添加用户操作日志
        NoteLog noteLog = new NoteLog();
        noteLog.setNoteId(noteId);
        noteLog.setUserId(UserContext.getUserId());
        if (deleteType == 1){
            if (isCreateNew == 0){//如果是新建的，那么就彻底删除
                //彻底删除
                noteMapper.completeDeleteNewCreateNote(noteId);
            }else {//如果不是新建的，那么就彻底删除
                //逻辑删除
                this.removeById(noteId);
            }
            noteLog.setAction(NoteActionEnums.DELETE_NOTE_LOGIC.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_NOTE_LOGIC.getActionDesc());
        }else if (deleteType == 2){
            //彻底删除
            noteMapper.completeDeleteNote(noteId);
            noteLog.setAction(NoteActionEnums.DELETE_NOTE_COMPLETE.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_NOTE_COMPLETE.getActionDesc());
        }else{
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        noteLogService.save(noteLog);
        return Result.success(ResponseEnums.Note_DELETE_LOGIC_SUCCESS);
    }
    //新增笔记
    @Override
    @Transactional
    public Result<Object> addNote() {
        //新增笔记
        Note note = new Note();
        note.setIsLock(0);
        note.setIsTop(0);
        note.setUserId(UserContext.getUserId());
        this.save(note);
        //日志
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        noteLog.setSmallNoteId(note.getId());
        noteLog.setAction(NoteActionEnums.USER_ADD_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_ADD_NOTE.getActionDesc());
        noteLogService.save(noteLog);
        return Result.success(ResponseEnums.Note_ADD_SUCCESS);
    }
}
