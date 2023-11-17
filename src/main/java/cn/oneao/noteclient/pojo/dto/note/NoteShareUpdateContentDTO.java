package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteShareUpdateContentDTO {
    private Integer noteShareId;
    private String title;
    private String tags;
    private String remark;
}
