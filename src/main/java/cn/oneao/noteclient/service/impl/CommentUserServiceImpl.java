package cn.oneao.noteclient.service.impl;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.mapper.CommentUserMapper;
import cn.oneao.noteclient.pojo.entity.comment.CommentUser;
import cn.oneao.noteclient.service.CommentUserService;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
public class CommentUserServiceImpl extends ServiceImpl<CommentUserMapper,CommentUser> implements CommentUserService {
    @Override
    public Result<Object> addCommentUser(CommentUser commentUser) {
        String commentUserEmail = commentUser.getCommentUserEmail();
        String commentUserName = commentUser.getCommentUserName();
        String commentUserAvatar = commentUser.getCommentUserAvatar();
        if(ObjectUtils.isEmpty(commentUserEmail) || ObjectUtils.isEmpty(commentUserName) || ObjectUtils.isEmpty(commentUserAvatar)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        LambdaQueryWrapper<CommentUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentUser::getCommentUserEmail,commentUserEmail);

        CommentUser queryCommentUser = this.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(queryCommentUser)) {
            //新增
            this.save(commentUser);
            return Result.success(commentUser);
        }else{
            //更新
            queryCommentUser.setCommentUserAvatar(commentUser.getCommentUserAvatar());
            queryCommentUser.setCommentUserName(commentUser.getCommentUserName());
            this.updateById(queryCommentUser);
        }
        return Result.success(queryCommentUser);
    }
    //获取用户的点赞信息
    @Override
    public Result<Object> getCommentUserLikes(Integer id) {
        if(ObjectUtils.isEmpty(id)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        CommentUser commentUser = this.getById(id);
        String commentLikes = commentUser.getCommentLikes();
        return Result.success(commentLikes);
    }
    //更新评论用户点赞的记录
    @Override
    @Transactional
    public Result<Object> goToLikes(Integer id, String likeIds) {
        if(ObjectUtils.isEmpty(id)){
            return Result.error(ResponseEnums.PARAMETER_MISSING);
        }
        CommentUser commentUser = this.getById(id);
        if (ObjectUtils.isEmpty(commentUser)){
            return Result.error(ResponseEnums.UNKNOWN_ERROR);
        }
        if (likeIds.equals("0")){
            likeIds = "";
        }
        commentUser.setCommentLikes(likeIds);
        this.updateById(commentUser);
        return Result.success();
    }
}
