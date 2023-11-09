package cn.oneao.noteclient.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class CommentsPageVO {
    private Integer total;
    private List<CommentsVO> result;
}
