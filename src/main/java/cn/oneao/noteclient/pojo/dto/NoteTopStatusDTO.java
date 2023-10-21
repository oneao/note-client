package cn.oneao.noteclient.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteTopStatusDTO {
    private Integer noteId;
    private Integer status;
}
