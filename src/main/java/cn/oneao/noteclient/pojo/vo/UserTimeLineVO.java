package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UserTimeLineVO {
    private String title;//操作标题
    private String noteTitle;//笔记
    private String noteBody;//笔记的主题内容
    private Date noteTime;//操作笔记的时间
}
