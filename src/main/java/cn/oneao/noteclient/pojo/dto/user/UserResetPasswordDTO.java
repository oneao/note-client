package cn.oneao.noteclient.pojo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResetPasswordDTO {
    private String oldPassword;
    private String newPassword;
}
