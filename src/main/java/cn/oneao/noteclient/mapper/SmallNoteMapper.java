package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.SmallNote;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmallNoteMapper extends BaseMapper<SmallNote> {
    void completeDeleteSmallNote(Integer smallNoteId);
}
