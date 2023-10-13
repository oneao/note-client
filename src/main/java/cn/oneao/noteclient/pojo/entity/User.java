package cn.oneao.noteclient.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String email;
    private String password;
    private String nickName;
    private String avatar;
    @TableField(fill = FieldFill.INSERT)
    private Integer level;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;
}
