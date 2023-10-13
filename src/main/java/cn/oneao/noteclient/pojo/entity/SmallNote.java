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
    private String title;   //标题
    private String tags;    //标签
    private String content; //内容
    private Integer userId; //用户id
    private Integer isTop;    //是否置顶(0:不置顶,1:置顶)
    @TableField(fill = FieldFill.INSERT)
    private Integer type;   //类别(0:小记,1:笔记)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;//创建时间
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;//更新时间
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;//是否删除(0:正常,1:逻辑删除,2:彻底删除)
    @TableField(fill = FieldFill.INSERT)
    private Integer isFinished;//是否完成(0:未完成,1:完成)
}
