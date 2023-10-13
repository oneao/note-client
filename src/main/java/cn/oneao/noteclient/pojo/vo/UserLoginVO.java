package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserLoginVO {
    private Integer id;
    private String email;
    private String nickName;
    private String avatar;
    private Integer level;
    private Date createTime;
}
