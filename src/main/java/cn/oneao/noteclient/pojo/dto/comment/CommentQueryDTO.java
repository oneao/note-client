package cn.oneao.noteclient.pojo.dto.comment;

import lombok.Data;

@Data
public class CommentQueryDTO {
    private Integer noteShareId;
    private Integer pageNum;//当前页数
    private Integer pageSize;//记录数
    private Integer isLatest;//是最新 1 还是最热 0
}
