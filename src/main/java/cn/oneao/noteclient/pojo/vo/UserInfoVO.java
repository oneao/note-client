package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {
    private Integer id;
    private String email;
    private String nickName;
    private String avatar;
    private Integer level;
    private Date createTime;
}
