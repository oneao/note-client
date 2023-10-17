package cn.oneao.noteclient.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmallNoteAddDTO {
    private Integer userId;//用户id
    private String smallNoteTitle;//小记标题
    private String smallNoteTags;//小记标签
    private String smallNoteEvents;//小记事件
    private String smallNoteRemark;//小记备注
    private Integer isTop;//小记是否置顶
    private Integer isFinished;//小记里面的标签是否全部完成(0:未完成，1:完成)
    private Integer isPrompt;//是否开启小记到期提示(0:不开启,1:开启)
    private Date beginTime;//到期提示的开始时间
    private Date endTime;//到期提示的结束时间
}
