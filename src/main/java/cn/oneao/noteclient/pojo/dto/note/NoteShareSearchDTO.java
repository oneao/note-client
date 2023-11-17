package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteShareSearchDTO {
    private String title;//笔记标题
    private Integer day;//分享天数
    private Integer isExpire;//是否过期
    private Integer page;//当前页
    private Integer pageSize;//当前页记录数
}
