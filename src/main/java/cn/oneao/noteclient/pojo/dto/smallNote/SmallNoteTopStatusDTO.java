package cn.oneao.noteclient.pojo.dto.smallNote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmallNoteTopStatusDTO {
    private Integer userId;
    private Integer smallNoteId;
    private Integer smallNoteTopStatus;
}
