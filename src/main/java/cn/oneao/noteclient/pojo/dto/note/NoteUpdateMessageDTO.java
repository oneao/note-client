package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteUpdateMessageDTO {
    private Integer noteId;
    private String noteTitle;
    private String noteBody;
    private String noteTags;
    private String noteBackgroundImage;
}
