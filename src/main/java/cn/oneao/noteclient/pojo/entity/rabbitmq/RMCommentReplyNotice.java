package cn.oneao.noteclient.pojo.entity.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RMCommentReplyNotice implements Serializable {
    private static final long serialVersionUID = 2L;
    private Integer userId;
    private Integer index;//序号
    private Date time;//评论时间
    private String message;//评论的笔记
}
