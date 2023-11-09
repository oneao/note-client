package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentsReplyVO {
    private Integer total;
    private List<CommentsVO> list;
}
