package cn.oneao.noteclient.pojo.dto.smallNote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SmallNoteDeleteDTO {
    private Integer userId;//用户id
    private Integer smallNoteId;//小记id
    private Integer deleteType; //删除状态，1逻辑删除，2彻底删除
}
