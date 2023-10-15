package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmallNoteVO {
    private Integer id; //id
    private Integer userId;//用户id
    private String smallNoteTitle;//小记标题
    private String smallNoteTags;//小记标签
    private Integer isTop;//小记是否置顶
    private Integer isFinished;//小记里面的标签是否全部完成(0:未完成，1:完成)
    private Integer isPrompt;//是否开启小记到期提示(0:不开启,1:开启)
    private Date beginTime;//到期提示的开始时间
    private Date endTime;//到期提示的结束时间
}
