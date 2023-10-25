package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class NoteShareAddDTO {
    private Integer noteId;//笔记id
    private Integer noteShareTime;//分享天数
    private String noteShareTitle;//分享标题
    private String noteShareTags;//分享标签
    private String noteShareRemark;//分享备注
    private Integer isNeedPassword;//是否需要笔记密码(0:不需要,1需要)
    private String noteSharePassword;//分享密码
}
