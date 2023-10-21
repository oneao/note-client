package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteOneVO {
    private Integer id; //主键id
    private String noteTitle;//笔记标题
    private String noteBody;//笔记小内容
    private String noteContent;//笔记全部内容
    private String noteTags;//笔记标签
    private Integer isLock;//是否加锁
    private Date updateTime;//更新时间
}
