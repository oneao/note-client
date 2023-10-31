package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmallNoteCalendarVO {
    private String noteTitle;
    private String smallNoteEvents;//小记事件
    private Date beginTime;//到期提示的开始时间
    private Date endTime;//到期提示的结束时间
}
