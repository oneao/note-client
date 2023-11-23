package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.annotations.ObserveUserLevel;
import cn.oneao.noteclient.constant.DocumentIndexConstant;
import cn.oneao.noteclient.enums.NoteActionEnums;
import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.mapper.NoteMapper;
import cn.oneao.noteclient.mapper.NoteShareMapper;
import cn.oneao.noteclient.pojo.dto.note.*;
import cn.oneao.noteclient.pojo.entity.UserLevel;
import cn.oneao.noteclient.pojo.entity.es.ESNote;
import cn.oneao.noteclient.pojo.entity.note.Note;
import cn.oneao.noteclient.pojo.entity.log.NoteLog;
import cn.oneao.noteclient.pojo.entity.note.NoteShare;
import cn.oneao.noteclient.pojo.vo.NoteCollectionVO;
import cn.oneao.noteclient.pojo.vo.NoteVO;
import cn.oneao.noteclient.server.DirectSender;
import cn.oneao.noteclient.service.NoteLogService;
import cn.oneao.noteclient.service.NoteService;
import cn.oneao.noteclient.service.UserLevelService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.RedisCache;
import cn.oneao.noteclient.utils.ResponseUtils.PageResult;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private NoteLogService noteLogService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private NoteShareMapper noteShareMapper;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private DirectSender directSender;
    @Autowired
    private ElasticsearchClient client;

    //获取笔记信息
    @Override
    public List<NoteVO> getNoteInfo() {
        int userId = UserContext.getUserId();
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId);
        queryWrapper.orderByDesc(Note::getIsTop);
        queryWrapper.orderByDesc(Note::getUpdateTime);
        List<Note> list = this.list(queryWrapper);
        List<NoteVO> noteVOList = new ArrayList<>();
        for (Note note : list) {
            NoteVO noteVO = new NoteVO();
            BeanUtils.copyProperties(note, noteVO);
            if (noteVO.getIsLock() == 1) {
                //加锁
                noteVO.setNoteBody("该笔记已加锁,暂时无法查看哦!");
                noteVO.setNoteTags(null);
            }
            if (ObjectUtils.isEmpty(noteVO.getUpdateTime())) {
                noteVO.setUpdateTime(note.getCreateTime());
            }
            noteVOList.add(noteVO);
        }
        return noteVOList;
    }

    @Override
    public Boolean verifyNoteLockPassword(NoteLockPassWordDTO noteVerifyLockPassWordDTO) {
        int userId = UserContext.getUserId();
        Integer noteId = noteVerifyLockPassWordDTO.getNoteId();
        String lockPassword = noteVerifyLockPassWordDTO.getLockPassword();
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getId, noteId).eq(Note::getUserId, userId).eq(Note::getLockPassword, lockPassword);
        Note noteOne = this.getOne(queryWrapper);
        if (!ObjectUtils.isEmpty(noteOne)) {
            String key = "temporary_access_note_token:" + userId + "-" + noteId;
            String value = "";
            redisCache.setCacheObject(key, value);
            return true;
        }
        return false;
    }

    //修改笔记的置顶状态
    @Override
    public void changeNoteTopStatus(NoteTopStatusDTO noteTopStatusDTO) {
        Integer noteId = noteTopStatusDTO.getNoteId();
        Integer status = noteTopStatusDTO.getStatus();
        LambdaUpdateWrapper<Note> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Note::getId, noteId);
        if (status == 1) {
            //取消置顶
            updateWrapper.set(Note::getIsTop, 0);
        } else if (status == 0) {
            //置顶
            updateWrapper.set(Note::getIsTop, 1);
        } else {
            //其他
            return;
        }
        this.update(new Note(), updateWrapper);
    }

    //删除笔记
    @Override
    @Transactional
    public Result<Object> deleteNote(NoteDeleteDTO noteDeleteDTO) {
        Integer noteId = noteDeleteDTO.getNoteId();
        Integer deleteType = noteDeleteDTO.getDeleteType();
        Integer isNewBuild = noteDeleteDTO.getIsNewBuild();
        if (ObjectUtils.isEmpty(noteId) || ObjectUtils.isEmpty(deleteType) || ObjectUtils.isEmpty(isNewBuild)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        //添加用户操作日志
        NoteLog noteLog = new NoteLog();
        noteLog.setNoteId(noteId);
        noteLog.setUserId(UserContext.getUserId());
        if (deleteType == 1) {
            if (isNewBuild == 0) {//如果是新建的，那么就彻底删除
                //彻底删除
                noteMapper.completeDeleteNewCreateNote(noteId);
                noteLog.setAction(NoteActionEnums.DELETE_NOTE_COMPLETE.getActionName());
                noteLog.setActionDesc(NoteActionEnums.DELETE_NOTE_COMPLETE.getActionDesc());
            } else {//如果不是新建的，那么就彻底删除
                //逻辑删除
                this.removeById(noteId);
                String noteShareKey = "NOTE_SHARE_USERID:" + UserContext.getUserId() + "_NOTEID:" + noteId;
                if (redisCache.hasKey(noteShareKey)) {
                    redisCache.deleteObject(noteShareKey);
                }
                NoteShare noteShare = noteShareMapper.selectOne(new LambdaQueryWrapper<NoteShare>().eq(NoteShare::getNoteId, noteId));
                if (!ObjectUtils.isEmpty(noteShare)) {
                    noteShareMapper.deleteById(noteShare);
                }
            }
            noteLog.setAction(NoteActionEnums.DELETE_NOTE_LOGIC.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_NOTE_LOGIC.getActionDesc());
            noteLogService.save(noteLog);
            //删除es中的文档
            directSender.sendDocumentDeleteNotice(noteId.toString());
            return Result.success(ResponseEnums.NOTE_DELETE_LOGIC_SUCCESS);
        } else if (deleteType == 2) {
            //彻底删除
            noteMapper.completeDeleteNote(noteId);
            noteLog.setAction(NoteActionEnums.DELETE_NOTE_COMPLETE.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_NOTE_COMPLETE.getActionDesc());
            noteLogService.save(noteLog);
            return Result.success(ResponseEnums.NOTE_DELETE_COMPLETE_SUCCESS);
        } else {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
    }

    //新增笔记
    @Override
    @Transactional
    public Result<Integer> addNote() {
        //新增笔记
        Note note = new Note();
        note.setUserId(UserContext.getUserId());
        this.save(note);
        //日志
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        noteLog.setNoteId(note.getId());
        noteLog.setAction(NoteActionEnums.USER_ADD_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_ADD_NOTE.getActionDesc());
        noteLogService.save(noteLog);
        return Result.success(note.getId(), ResponseEnums.NOTE_ADD_SUCCESS);
    }

    //获取笔记
    @Override
    public Result<Object> getNoteById(Integer noteId) {
        if (ObjectUtils.isEmpty(noteId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        Note note = this.getById(noteId);
        if (note.getUserId() != UserContext.getUserId()) {
            return Result.error("获取失败");
        }
        if (ObjectUtils.isEmpty(note)) {
            return Result.error("暂无笔记哦!");
        }
        //如果上锁
        if (note.getIsLock() == 1) {
            String key = "temporary_access_note_token:" + UserContext.getUserId() + "-" + noteId;
            boolean flag = redisCache.hasKey(key);
            if (!flag) {
                //如果没有进行密码验证，则无法访问到该笔记
                return Result.error(ResponseEnums.NOTE_NEED_PASSWORD);
            }
            redisCache.deleteObject(key);   //删除
        }
        NoteVO noteVO = new NoteVO();
        BeanUtils.copyProperties(note, noteVO);
        return Result.success(noteVO);
    }

    //为笔记添加密码
    @Override
    @Transactional
    public Result<Object> addNoteLockPassword(NoteLockPassWordDTO noteLockPassWordDTO) {
        String lockPassword = noteLockPassWordDTO.getLockPassword();
        Integer noteId = noteLockPassWordDTO.getNoteId();
        LambdaUpdateWrapper<Note> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Note::getId, noteId);
        updateWrapper.set(Note::getIsLock, 1);
        updateWrapper.set(Note::getLockPassword, lockPassword);
        this.update(new Note(), updateWrapper);
        //日志
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        noteLog.setNoteId(noteId);
        noteLog.setAction(NoteActionEnums.USER_UPDATE_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_UPDATE_NOTE.getActionDesc());
        noteLogService.save(noteLog);
        return Result.success(ResponseEnums.NOTE_ADD_LOCK_SUCCESS);
    }

    //彻底删除笔记密码
    @Override
    public Result<Object> completelyLiftedNoteLockPassword(NoteLockPassWordDTO noteLockPassWordDTO) {
        Integer noteId = noteLockPassWordDTO.getNoteId();
        String lockPassword = noteLockPassWordDTO.getLockPassword();
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getId, noteId);
        queryWrapper.eq(Note::getIsLock, 1);
        queryWrapper.eq(Note::getLockPassword, lockPassword);
        Note note = this.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(note)) {
            return Result.error(ResponseEnums.NOTE_VERIFY_LOCK_ERROR);
        }
        //更新
        if (note.getIsLock() == 1) {
            String key = "temporary_access_note_token:" + UserContext.getUserId() + "-" + noteId;
            //如果上锁,就需要移除
            if (redisCache.hasKey(key)) {
                //删除
                redisCache.deleteObject(key);
            }
        }
        LambdaUpdateWrapper<Note> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Note::getId, noteId);
        updateWrapper.set(Note::getIsLock, 0);
        updateWrapper.set(Note::getLockPassword, null);
        this.update(updateWrapper);
        //日志
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        noteLog.setNoteId(noteId);
        noteLog.setAction(NoteActionEnums.USER_UPDATE_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_UPDATE_NOTE.getActionDesc());
        noteLogService.save(noteLog);
        return Result.success(ResponseEnums.NOTE_DELETE_LOCK_SUCCESS);
    }

    //更新笔记内容
    @Transactional
    @Override
    public Result<Object> updateNoteContent(NoteUpdateContentDTO noteUpdateContentDTO) {
        Integer noteId = noteUpdateContentDTO.getNoteId();
        String noteBody = noteUpdateContentDTO.getNoteBody();
        String noteContent = noteUpdateContentDTO.getNoteContent();
        if (ObjectUtils.isEmpty(noteId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        Note note = this.getById(noteId);
        note.setNoteBody(noteBody);
        note.setNoteContent(noteContent);
        this.updateById(note);
        //更新到es里面
        ESNote esNote = new ESNote();
        esNote.setId(noteId.toString());
        esNote.setNoteId(noteId);
        esNote.setUserId(UserContext.getUserId());
        esNote.setTitle(note.getNoteTitle());
        esNote.setContent(noteContent);
        esNote.setUpdateTime(note.getUpdateTime());
        directSender.sendDocumentInsertOrUpdateNotice(esNote);
        //更新日志操作表
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        noteLog.setNoteId(noteId);
        noteLog.setAction(NoteActionEnums.USER_UPDATE_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_UPDATE_NOTE.getActionDesc());
        noteLogService.save(noteLog);
        return Result.success(ResponseEnums.NOTE_UPDATE_SUCCESS);
    }

    //更新笔记的收藏状态
    @Override
    @Transactional
    @ObserveUserLevel
    public Result<Object> updateNoteCollection(NoteUpdateCollectionDTO updateCollectionDTO) {
        Integer noteId = updateCollectionDTO.getNoteId();
        Integer isCollection = updateCollectionDTO.getIsCollection();
        LambdaUpdateWrapper<Note> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Note::getId, noteId);
        updateWrapper.set(Note::getIsCollection, isCollection);
        //更新UserLevel表
        UserLevel userLevel = userLevelService.getOne(new LambdaQueryWrapper<UserLevel>().eq(UserLevel::getUserId, UserContext.getUserId()));
        Integer collectionNoteNumber = userLevel.getCollectionNoteNumber();
        if (isCollection == 1) {
            userLevel.setCollectionNoteNumber(collectionNoteNumber + 1);
            userLevelService.updateById(userLevel);
            this.update(new Note(), updateWrapper);
            return Result.success(ResponseEnums.NOTE_COLLECTION_SUCCESS);
        } else if (isCollection == 0) {
            if (collectionNoteNumber > 0) {
                userLevel.setCollectionNoteNumber(collectionNoteNumber - 1);
                userLevelService.updateById(userLevel);
            }
            this.update(new Note(), updateWrapper);
            return Result.success(ResponseEnums.NOTE_COLLECTION_CANCEL_SUCCESS);
        }
        return Result.error(ResponseEnums.PARAMETER_MISSING);
    }

    //更新用户的笔记信息
    @Override
    @Transactional
    public Result<Object> updateNoteMessage(NoteUpdateMessageDTO noteUpdateMessageDTO) {
        Integer noteId = noteUpdateMessageDTO.getNoteId();
        if (ObjectUtils.isEmpty(noteId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        Note note = this.getById(noteId);
        note.setNoteTitle(noteUpdateMessageDTO.getNoteTitle());
        note.setNoteBody(noteUpdateMessageDTO.getNoteBody());
        note.setNoteTags(noteUpdateMessageDTO.getNoteTags());
        note.setNoteBackgroundImage(noteUpdateMessageDTO.getNoteBackgroundImage());
        this.updateById(note);
        //更新到es里面
        ESNote esNote = new ESNote();
        esNote.setId(noteId.toString());
        esNote.setNoteId(noteId);
        esNote.setUserId(UserContext.getUserId());
        esNote.setTitle(note.getNoteTitle());
        esNote.setContent(note.getNoteContent());
        esNote.setUpdateTime(note.getUpdateTime());
        directSender.sendDocumentInsertOrUpdateNotice(esNote);

        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(UserContext.getUserId());
        noteLog.setNoteId(noteId);
        noteLog.setAction(NoteActionEnums.USER_UPDATE_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_UPDATE_NOTE.getActionDesc());
        noteLogService.save(noteLog);
        return Result.success(ResponseEnums.NOTE_UPDATE_SUCCESS);
    }

    //获取收藏的笔记
    @Override
    public Result<Object> getCollectionNote(NoteCollectionSearchDTO noteCollectionSearchDTO) {
        String noteTitle = noteCollectionSearchDTO.getNoteTitle();
        String noteBody = noteCollectionSearchDTO.getNoteBody();
        Integer page = noteCollectionSearchDTO.getPage();
        Integer pageSize = noteCollectionSearchDTO.getPageSize();

        int userId = UserContext.getUserId();
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Note::getUserId, userId);
        queryWrapper.eq(Note::getIsCollection, 1);
        if (!ObjectUtils.isEmpty(noteTitle)) {
            queryWrapper.like(Note::getNoteTitle, noteTitle);
        }
        if (!ObjectUtils.isEmpty(noteBody)) {
            queryWrapper.like(Note::getNoteBody, noteBody);
        }
        Page<Note> pageModel = new Page<>(page, pageSize);
        Page<Note> pageResult = noteMapper.selectPage(pageModel, queryWrapper);
        long total = pageResult.getTotal();//总记录数
        List<Note> list = pageResult.getRecords();//记录

        List<NoteCollectionVO> noteCollectionVOList = getNoteCollectionVOS(list);
        PageResult<NoteCollectionVO> result = new PageResult<>();
        result.setRecord(noteCollectionVOList);
        result.setTotal(total);
        return Result.success(result);
    }

    //取消收藏一个笔记
    @Override
    @Transactional
    @ObserveUserLevel
    public Result<Object> cancelCollectionNote(Integer noteId) {
        if (ObjectUtils.isEmpty(noteId)) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        Note note = this.getById(noteId);
        if (ObjectUtils.isEmpty(note) || ObjectUtils.isEmpty(UserContext.getUserId()) || !note.getUserId().equals(UserContext.getUserId())) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        //更新 取消收藏
        note.setIsCollection(0);
        this.updateById(note);
        //更新UserLevel表
        UserLevel userLevel = userLevelService.getOne(new LambdaQueryWrapper<UserLevel>().eq(UserLevel::getUserId, UserContext.getUserId()));
        Integer collectionNoteNumber = userLevel.getCollectionNoteNumber();
        if (collectionNoteNumber > 0) {
            userLevel.setLevel(collectionNoteNumber - 1);
        }
        userLevelService.updateById(userLevel);
        return Result.success();
    }

    //批量取消收藏笔记
    @Override
    @Transactional
    @ObserveUserLevel
    public Result<Object> batchCancelCollectionNotes(List<Integer> noteIds) {
        if (noteIds.isEmpty()) {
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        for (Integer noteId : noteIds) {
            Note note = this.getById(noteId);
            if (ObjectUtils.isEmpty(note)) {
                continue;
            } else {
                note.setIsCollection(0);
                this.updateById(note);
            }
        }
        //更新UserLevel表
        UserLevel userLevel = userLevelService.getOne(new LambdaQueryWrapper<UserLevel>().eq(UserLevel::getUserId, UserContext.getUserId()));
        Integer collectionNoteNumber = userLevel.getCollectionNoteNumber();
        if (collectionNoteNumber > noteIds.size() - 1) {
            userLevel.setLevel(collectionNoteNumber - noteIds.size());
        }
        userLevelService.updateById(userLevel);
        return Result.success();
    }

    //获取es笔记中的内容
    @Override
    public Result<Object> getElasticSearchValue(String value) {
        List<ESNote> list = new ArrayList<>();
        SearchResponse<ESNote> search = null;
        try {
            search = client.search(s -> s
                            .index(DocumentIndexConstant.NOTE_INDEX)
                            .query(q -> q
                                    .bool(b -> b
                                            .filter(f -> f
                                                    .term(t -> t
                                                            .field("userId")
                                                            .value(UserContext.getUserId())
                                                    ))
                                            .must(m -> m
                                                    .match(t -> t
                                                            .field("content")
                                                            .query(value)
                                                            .analyzer("ik_max_word") // 设置分词器为IK分词器
                                                    ))
                                    ))
                            .highlight(h -> h
                                    .fields("content", f -> f
                                            .preTags("<mark>")
                                            .postTags("</mark>")
                                    )
                            )
                            .sort(f -> f.field(o -> o.field("updateTime").order(SortOrder.Desc)))
                    , ESNote.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Hit<ESNote> hit : search.hits().hits()) {
            ESNote esNote = hit.source();
            assert esNote != null;
            Map<String, List<String>> highlightFields = hit.highlight();
            if (highlightFields.containsKey("content")) {
                List<String> contentHighlights = highlightFields.get("content");
                // 获取高亮内容，并将其应用到实体对象
                esNote.setContent(contentHighlights.get(0));
            }
            log.info("esNoteContent: {}", esNote.getContent());
            list.add(esNote);
        }
        return Result.success(list);
    }

    //获取视图笔记列表
    @NotNull
    private static List<NoteCollectionVO> getNoteCollectionVOS(List<Note> list) {
        List<NoteCollectionVO> noteCollectionVOList = new ArrayList<>();
        int serialNumber = 1;
        for (Note note : list) {
            NoteCollectionVO noteCollectionVO = new NoteCollectionVO();
            noteCollectionVO.setId(note.getId());
            noteCollectionVO.setTitle(note.getNoteTitle());
            noteCollectionVO.setTags(note.getNoteTags());
            noteCollectionVO.setNoteBody(note.getNoteBody());
            noteCollectionVO.setContent(note.getNoteContent());
            noteCollectionVO.setSerialNumber(serialNumber++);
            noteCollectionVOList.add(noteCollectionVO);
        }
        return noteCollectionVOList;
    }
}
