package cn.oneao.noteclient.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteDeleteDTO {
    private Integer noteId;
    private Integer deleteType;
    private Integer isCreateNew;
}
