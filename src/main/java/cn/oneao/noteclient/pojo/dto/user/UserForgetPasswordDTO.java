package cn.oneao.noteclient.pojo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserForgetPasswordDTO {
    private String email;
    private String code;
    private String newPassword;
}
