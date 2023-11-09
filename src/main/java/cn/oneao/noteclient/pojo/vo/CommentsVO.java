package cn.oneao.noteclient.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CommentsVO {
    private Integer id;//评论表的id
    private Integer parentId;//父级id
    private Integer uid;//评论用户表的id
    private String address;//地址
    private String content;//内容
    private Integer likes;//点赞数
    private String contentImg;//内容里面的图片
    private Date createTime;//创建时间
    private CommentUserVO user;//发这条记录的用户
    private CommentsReplyVO reply;//评论子节点
}
