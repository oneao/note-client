package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteShareGetDTO {
    private Integer n_sid;//分享表中的主键id
    private String noteSharePassword;//分享笔记的密码,可能为空!
}
