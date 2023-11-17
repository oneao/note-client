package cn.oneao.noteclient.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_level")
public class UserLevel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;//用户id
    private Integer level;//默认1
    private Integer collectionNoteNumber;//收藏笔记的数量
    private Integer shareNoteNumber;//分享笔记的数量
    private Integer shareNoteVisitNumber;//分享笔记的访问总数量
    private Integer shareNoteLikeNumber;//分享笔记的点赞总数量
    private Integer shareNoteCommentNumber;//分享笔记的评论总数量
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;//更新时间
}
