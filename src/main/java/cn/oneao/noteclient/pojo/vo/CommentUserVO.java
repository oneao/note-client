package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUserVO {
    private String username;
    private String avatar;
    private Integer level;
}
