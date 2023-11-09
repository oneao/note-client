package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.comment.CommentAddDTO;
import cn.oneao.noteclient.pojo.dto.comment.CommentQueryDTO;
import cn.oneao.noteclient.pojo.entity.comment.Comment;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface CommentService extends IService<Comment> {
    //新增评论
    Result<Object> addComment(HttpServletRequest httpServletRequest, MultipartFile multipartFile, CommentAddDTO commentAddDTO);
    //获取评论列表
    Result<Object> getComments(CommentQueryDTO commentQueryDTO);
    //点赞
    Result<Object> goToLike(Integer id, Integer isLike);
}
