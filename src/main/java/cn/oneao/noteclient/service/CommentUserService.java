package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.entity.comment.CommentUser;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentUserService extends IService<CommentUser> {
    Result<Object> addCommentUser(CommentUser commentUser);
    //获取用户的点赞信息
    Result<Object> getCommentUserLikes(Integer id);
    //点赞信息
    Result<Object> goToLikes(Integer id, String likeIds);
}
