package cn.oneao.noteclient.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteVerifyLockPassWordDTO {
    private Integer noteId;
    private String lockPassword;
}
