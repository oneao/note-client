package cn.oneao.noteclient.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 小记实体类
 */
@TableName("small_note")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmallNote {
    @TableId(type = IdType.AUTO)
    private Integer id; //id
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
    private Date createTime;//小记创建时间
    private Date updateTime;//小记更新时间
    private Integer isDelete;//是否删除(0：正常，1：删除，2：彻底删除)
}
