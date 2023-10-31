package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.SmallNote;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SmallNoteMapper extends BaseMapper<SmallNote> {
    void completeDeleteSmallNote(Integer smallNoteId);
    void updateSmallNoteRecoverOne(@Param("smallNoteId") Integer smallNoteId);
    void completeDeleteSmallNotes(@Param("smallNoteIds") List<Integer> smallNoteIds);
    //批量恢复
    void recoverMany(@Param("smallNoteIds")List<Integer> smallNoteIds);
}
