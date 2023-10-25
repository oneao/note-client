package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteVO {
    private Integer id; //主键id
    private String noteTitle;//笔记标题
    private String noteBody;//笔记小内容
    private String noteContent;//笔记全部内容
    private String noteTags;//笔记标签
    private Integer isTop;//是否置顶
    private Integer isLock;//是否加锁
    private Integer isNewBuild;//是否新建(可以直接删除，不存在回收在中)
    private Integer isCollection;//(0:未收藏，1:收藏)
    private Date updateTime;
}
