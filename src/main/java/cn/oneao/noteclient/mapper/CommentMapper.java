package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.comment.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
