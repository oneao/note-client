package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.User;
import cn.oneao.noteclient.pojo.vo.RecentOperationNoteVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    List<RecentOperationNoteVO> getRecentOperationNote(@Param("userId") int userId);
}
