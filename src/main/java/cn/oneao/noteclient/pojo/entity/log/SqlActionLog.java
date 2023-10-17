package cn.oneao.noteclient.pojo.entity.log;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@TableName("sql_action_log")
public class SqlActionLog {
    private Integer userId;//用户id
    private String requestMapping;//请求路径
    private String requestClass;//请求入口类名
    private String requestMethod;//请求入口方法名
    private String requestParam;//请求入口参数
    private String sqlInterfaceName;//请求接口名
    private String sqlStatements;//方法中执行的sql语句
    private String sqlParams;//sql中的参数
    private Long executionTime;//方法执行耗时
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;//存储的时间
}
