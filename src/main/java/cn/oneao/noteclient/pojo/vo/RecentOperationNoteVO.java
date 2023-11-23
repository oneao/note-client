package cn.oneao.noteclient.pojo.vo;



import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//小记或者笔记
@Data
@NoArgsConstructor
public class RecentOperationNoteVO{
    private Integer id;//笔记或小记id
    private String title;//标题
    private Integer type;//类别（0小记，1笔记）
    private Date updateTime;//更新时间
}
