package cn.oneao.noteclient.pojo.dto.comment;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommentAddDTO {
    private Integer noteShareId;
    private Integer parentId;
    private Integer commentUserId;
    private String content;
}
