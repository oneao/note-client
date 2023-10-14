package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.enums.NoteActionEnums;
import cn.oneao.noteclient.mapper.SmallNoteMapper;
import cn.oneao.noteclient.pojo.dto.SmallNoteDeleteDTO;
import cn.oneao.noteclient.pojo.dto.SmallNotePageDTO;
import cn.oneao.noteclient.pojo.dto.SmallNoteTopStatusDTO;
import cn.oneao.noteclient.pojo.entity.NoteLog;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.service.NoteLogService;
import cn.oneao.noteclient.service.SmallNoteService;
import cn.oneao.noteclient.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
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
        queryWrapper.eq(SmallNote::getIsDelete, 0);
        queryWrapper.orderByDesc(SmallNote::getIsTop);
        queryWrapper.orderByAsc(SmallNote::getIsFinished);
        queryWrapper.orderByDesc(SmallNote::getCreateTime);
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
                //置顶
                LambdaQueryWrapper<SmallNote> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SmallNote::getIsTop, 1);
                SmallNote smallNote = smallNoteMapper.selectOne(queryWrapper);
                //判断是否有已经置顶的小记
                if (!ObjectUtils.isEmpty(smallNote)) {
                    //如果有的话，则取消置顶
                    smallNote.setIsTop(0);
                    smallNoteMapper.updateById(smallNote);
                }
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
}
