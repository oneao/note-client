package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.enums.NoteActionEnums;
import cn.oneao.noteclient.mapper.SmallNoteMapper;
import cn.oneao.noteclient.pojo.dto.*;
import cn.oneao.noteclient.pojo.entity.log.NoteLog;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteOneVO;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.service.NoteLogService;
import cn.oneao.noteclient.service.SmallNoteService;
import cn.oneao.noteclient.utils.ResponseUtils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmallNoteServiceImpl extends ServiceImpl<SmallNoteMapper, SmallNote> implements SmallNoteService {
    @Autowired
    private SmallNoteMapper smallNoteMapper;
    @Autowired
    private NoteLogService noteLogService;
    /**
     * 查询小记列表
     *
     * @param smallNotePageDTO 用户id
     * @return 小记列表
     */
    @Override
    public PageResult<SmallNoteVO> getSmallNoteInfo(SmallNotePageDTO smallNotePageDTO) {
        PageResult<SmallNoteVO> smallNoteVOPageResult = new PageResult<>();
        Integer userId = smallNotePageDTO.getUserId();
        Integer page = smallNotePageDTO.getPage();
        Integer pageSize = smallNotePageDTO.getPageSize();

        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(page) || ObjectUtils.isEmpty(pageSize)) {
            return null;
        }

        Page<SmallNote> pageModal = new Page<>(page, pageSize);
        LambdaQueryWrapper<SmallNote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmallNote::getUserId, userId);
        queryWrapper.orderByDesc(SmallNote::getIsTop);
        queryWrapper.orderByAsc(SmallNote::getIsFinished);
        queryWrapper.orderByDesc(SmallNote::getCreateTime);
        if (!ObjectUtils.isEmpty(smallNotePageDTO.getFilterValue())){
            queryWrapper.eq(SmallNote::getIsFinished,smallNotePageDTO.getFilterValue());
        }
        if(!ObjectUtils.isEmpty(smallNotePageDTO.getSearchValue())){
            queryWrapper.like(SmallNote::getSmallNoteTitle,smallNotePageDTO.getSearchValue());
        }
        Page<SmallNote> smallNotePage = smallNoteMapper.selectPage(pageModal, queryWrapper);

        List<SmallNote> smallNotes = smallNotePage.getRecords();
        long total = smallNotePage.getTotal();

        List<SmallNoteVO> smallNoteVOS = new ArrayList<>();
        if (!ObjectUtils.isEmpty(smallNotes)) {
            for (SmallNote smallNote : smallNotes) {
                SmallNoteVO smallNoteVO = new SmallNoteVO();
                BeanUtils.copyProperties(smallNote, smallNoteVO);
                smallNoteVOS.add(smallNoteVO);
            }
            smallNoteVOPageResult.setRecord(smallNoteVOS);
            smallNoteVOPageResult.setTotal(total);

            return smallNoteVOPageResult;
        }
        return smallNoteVOPageResult;
    }
    /**
     * 更新小记置顶状态
     * @param smallNoteTopStatusDTO:前端请求类
     */
    @Override
    public void changeSmallNoteStatus (SmallNoteTopStatusDTO smallNoteTopStatusDTO){
        Integer smallNoteTopStatus = smallNoteTopStatusDTO.getSmallNoteTopStatus();
        Integer userId = smallNoteTopStatusDTO.getUserId();
        Integer smallNoteId = smallNoteTopStatusDTO.getSmallNoteId();
        if (!ObjectUtils.isEmpty(smallNoteTopStatus)) {
            if (smallNoteTopStatus == 1) {
                //取消置顶
                LambdaUpdateWrapper<SmallNote> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SmallNote::getUserId, userId);
                updateWrapper.eq(SmallNote::getId, smallNoteId);
                updateWrapper.set(SmallNote::getIsTop, 0);
                //更新
                this.update(updateWrapper);
            } else if (smallNoteTopStatus == 0) {
                //更新小记状态
                LambdaUpdateWrapper<SmallNote> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SmallNote::getUserId, userId);
                updateWrapper.eq(SmallNote::getId, smallNoteId);
                updateWrapper.set(SmallNote::getIsTop, 1);
                this.update(updateWrapper);
            }
        }
    }
    /**
     * 删除小记
     * @param smallNoteDeleteDTO 删除小记对象
     */
    @Transactional
    @Override
    public void deleteSmallNote(SmallNoteDeleteDTO smallNoteDeleteDTO) {
        Integer userId = smallNoteDeleteDTO.getUserId();
        Integer smallNoteId = smallNoteDeleteDTO.getSmallNoteId();
        Integer deleteType = smallNoteDeleteDTO.getDeleteType();
        if(ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(smallNoteId) || ObjectUtils.isEmpty(deleteType)){
            return;
        }
        if(deleteType == 1){
            //逻辑删除
            this.removeById(smallNoteId);
        }else{
            //彻底删除
            smallNoteMapper.completeDeleteSmallNote(smallNoteId);
        }
        //添加日志
        this.addNoteActionLog(smallNoteDeleteDTO);
    }
    //添加操作日志
    public void addNoteActionLog(SmallNoteDeleteDTO smallNoteDeleteDTO){
        NoteLog noteLog = new NoteLog();
        noteLog.setUserId(smallNoteDeleteDTO.getUserId());
        noteLog.setSmallNoteId(smallNoteDeleteDTO.getSmallNoteId());
        if (smallNoteDeleteDTO.getDeleteType() == 1){
            //逻辑删除
            noteLog.setAction(NoteActionEnums.DELETE_SMALL_NOTE_LOGIC.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_SMALL_NOTE_LOGIC.getActionDesc());
        }else{
            //彻底删除
            noteLog.setAction(NoteActionEnums.DELETE_SMALL_NOTE_COMPLETE.getActionName());
            noteLog.setActionDesc(NoteActionEnums.DELETE_SMALL_NOTE_COMPLETE.getActionDesc());
        }
        noteLogService.save(noteLog);
    }
    /**
     * 新增小记对象
     * @param smallNoteAddDTO 添加对象
     */
    @Transactional
    @Override
    public void addSmallNote(SmallNoteAddDTO smallNoteAddDTO) {
        SmallNote smallNote = new SmallNote();
        BeanUtils.copyProperties(smallNoteAddDTO,smallNote);
        if (0 == smallNote.getIsPrompt()){
            smallNote.setBeginTime(null);
            smallNote.setEndTime(null);
        }
        this.save(smallNote);
        //添加小记操作日志
        NoteLog noteLog = new NoteLog();
        noteLog.setSmallNoteId(smallNote.getId());
        noteLog.setUserId(smallNote.getUserId());
        noteLog.setAction(NoteActionEnums.USER_ADD_SMALL_NOTE.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_ADD_SMALL_NOTE.getActionDesc());
        noteLogService.save(noteLog);
    }
    /**
     * 获取一个smallNote对象
     * @param smallNoteId 小记id
     * @return 返回该小记的信息
     */
    @Override
    public SmallNoteOneVO getOneSmallNote(Integer smallNoteId) {
        if (ObjectUtils.isEmpty(smallNoteId)){
            return null;
        }
        SmallNote smallNote = this.getById(smallNoteId);
        SmallNoteOneVO smallNoteOneVO = new SmallNoteOneVO();
        BeanUtils.copyProperties(smallNote,smallNoteOneVO);
        return smallNoteOneVO;
    }
    /**
     * 更新小记
     * @param smallNoteUpdateDTO 更新小记对象
     */
    @Transactional
    @Override
    public void updateSmallNote(SmallNoteUpdateDTO smallNoteUpdateDTO) {
        //先删除，后更新
        Integer smallNoteId = smallNoteUpdateDTO.getSmallNoteId();
        if(ObjectUtils.isEmpty(smallNoteId)){
            return;
        }
        //删除，再添加
        this.removeById(smallNoteId);
        //删除日志
        NoteLog delNoteLog = new NoteLog();
        delNoteLog.setSmallNoteId(smallNoteId);
        delNoteLog.setUserId(smallNoteUpdateDTO.getUserId());
        delNoteLog.setAction(NoteActionEnums.SYSTEM_LOGIN_DELETE_SmallNote.getActionName());
        delNoteLog.setActionDesc(NoteActionEnums.SYSTEM_LOGIN_DELETE_SmallNote.getActionDesc());
        noteLogService.save(delNoteLog);
        //创建SmallNote对象
        SmallNote smallNote = new SmallNote();
        BeanUtils.copyProperties(smallNoteUpdateDTO,smallNote);
        if (0 == smallNote.getIsPrompt()){
            smallNote.setBeginTime(null);
            smallNote.setEndTime(null);
        }
        this.save(smallNote);
        /* 添加日志 */
        NoteLog noteLog = new NoteLog();
        noteLog.setSmallNoteId(smallNote.getId());
        noteLog.setUserId(smallNote.getUserId());
        noteLog.setAction(NoteActionEnums.USER_UPDATE_SmallNote.getActionName());
        noteLog.setActionDesc(NoteActionEnums.USER_UPDATE_SmallNote.getActionDesc());
        noteLogService.save(noteLog);
    }
}
