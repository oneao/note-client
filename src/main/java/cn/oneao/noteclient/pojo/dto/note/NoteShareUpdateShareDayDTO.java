package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteShareUpdateShareDayDTO {
    private Integer noteShareId;
    private Integer shareDay;
}
