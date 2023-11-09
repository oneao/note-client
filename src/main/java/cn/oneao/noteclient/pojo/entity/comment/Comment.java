package cn.oneao.noteclient.pojo.entity.comment;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer id;//id
    private Integer parentId;//父级id
    private Integer noteShareId;//分享笔记表id
    private Integer commentUserId;//评论用户表id
    private String address;//地址
    private String content;//笔记内容
    private Integer likes;//点赞数
    private String contentImg;//内容里面的图片
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;//创建时间
}
