package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.mapper.SmallNoteMapper;
import cn.oneao.noteclient.pojo.entity.SmallNote;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.service.SmallNoteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
}
