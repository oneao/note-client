package cn.oneao.noteclient.pojo.dto.smallNote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SmallNotePageDTO {
    private Integer userId;
    private Integer page;
    private Integer pageSize;
    private Integer filterValue;
    private String searchValue;
}
