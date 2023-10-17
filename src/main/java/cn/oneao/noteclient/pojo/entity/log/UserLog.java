package cn.oneao.noteclient.pojo.entity.log;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_log")
public class UserLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String action;
    private String actionDesc;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
