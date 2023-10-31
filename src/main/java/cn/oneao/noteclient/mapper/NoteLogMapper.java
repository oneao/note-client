package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.log.NoteLog;
import cn.oneao.noteclient.pojo.vo.UserTimeLineVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteLogMapper extends BaseMapper<NoteLog> {
    List<UserTimeLineVO> getNoteActionMessage(@Param("userId") Integer userId);

    List<UserTimeLineVO> getSmallNoteActionMessage(@Param("userId") Integer userId);
}
