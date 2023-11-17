package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteShareLockDTO {
    private Integer noteShareId;
    private String lockPassword;
}
