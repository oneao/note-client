package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.mapper.SmallNoteMapper;
import cn.oneao.noteclient.pojo.dto.SmallNoteTopStatusDTO;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.service.SmallNoteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmallNoteServiceImpl extends ServiceImpl<SmallNoteMapper, SmallNote> implements SmallNoteService {
    @Autowired
    private SmallNoteMapper smallNoteMapper;

    /**
     * 查询小记列表
     * @param userId 用户id
     * @return 小记列表
     */
    @Override
    public List<SmallNoteVO> getSmallNoteInfo(Integer userId) {
        if (ObjectUtils.isEmpty(userId)){
            return null;
        }
        LambdaQueryWrapper<SmallNote> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmallNote::getUserId,userId);
        queryWrapper.eq(SmallNote::getIsDelete,0);
        queryWrapper.orderByDesc(SmallNote::getIsTop);
        queryWrapper.orderByAsc(SmallNote::getIsFinished);
        queryWrapper.orderByDesc(SmallNote::getCreateTime);
        List<SmallNote> smallNotes = smallNoteMapper.selectList(queryWrapper);
        List<SmallNoteVO> smallNoteVOS = new ArrayList<>();
        if(!ObjectUtils.isEmpty(smallNotes)){
            for (SmallNote smallNote : smallNotes) {
                SmallNoteVO smallNoteVO = new SmallNoteVO();
                BeanUtils.copyProperties(smallNote,smallNoteVO);
                smallNoteVOS.add(smallNoteVO);
            }
        }
        return smallNoteVOS;
    }

    /**
     * 更新小记置顶状态
     * @param smallNoteTopStatusDTO:前端请求类
     */
    @Override
    public void changeSmallNoteStatus(SmallNoteTopStatusDTO smallNoteTopStatusDTO) {
        Integer smallNoteTopStatus = smallNoteTopStatusDTO.getSmallNoteTopStatus();
        Integer userId = smallNoteTopStatusDTO.getUserId();
        Integer smallNoteId = smallNoteTopStatusDTO.getSmallNoteId();
        if(!ObjectUtils.isEmpty(smallNoteTopStatus)){
            if (smallNoteTopStatus == 1){
                //取消置顶
                LambdaUpdateWrapper<SmallNote> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SmallNote::getUserId,userId);
                updateWrapper.eq(SmallNote::getId,smallNoteId);
                updateWrapper.set(SmallNote::getIsTop,0);
                //更新
                this.update(updateWrapper);
            }else if(smallNoteTopStatus == 0){
                //置顶
                LambdaQueryWrapper<SmallNote> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SmallNote::getIsTop,1);
                SmallNote smallNote = smallNoteMapper.selectOne(queryWrapper);
                //判断是否有已经置顶的小记
                if(!ObjectUtils.isEmpty(smallNote)){
                    //如果有的话，则取消置顶
                    smallNote.setIsTop(0);
                    smallNoteMapper.updateById(smallNote);
                }
                //更新小记状态
                LambdaUpdateWrapper<SmallNote> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SmallNote::getUserId,userId);
                updateWrapper.eq(SmallNote::getId,smallNoteId);
                updateWrapper.set(SmallNote::getIsTop,1);
                this.update(updateWrapper);
            }
        }
    }
}
