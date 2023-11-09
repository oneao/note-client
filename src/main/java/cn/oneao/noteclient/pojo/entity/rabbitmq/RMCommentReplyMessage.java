package cn.oneao.noteclient.pojo.entity.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RMCommentReplyMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;//邮箱
    private String link;//链接
    private String comment;//那一条评论被回复了
}
