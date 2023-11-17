package cn.oneao.noteclient.pojo.dto.note;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NoteCollectionSearchDTO {
    private String noteTitle;
    private String noteBody;
    private Integer page;
    private Integer pageSize;
}
