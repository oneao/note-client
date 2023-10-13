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
    private String title;   //标题
    private String tags;    //标签
    private Integer isTop;    //是否置顶(0:不置顶,1:置顶)
    private Date createTime;//创建时间
    private Integer isFinished;//是否完成(0:未完成,1:完成)
}
