package cn.oneao.noteclient.pojo.entity.comment;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("comment_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUser {
    @TableId(type = IdType.AUTO)
    private Integer id;//id
    private String commentUserEmail;//评论用户邮箱
    private String commentUserName;//评论用户昵称
    private String commentUserAvatar;//评论用户头像
    @TableField(fill = FieldFill.INSERT)
    private Integer commentUserLevel;//等级 默认等级为1
    private Integer commentCount;//评论次数
        private String commentLikes;//评论点赞
}
