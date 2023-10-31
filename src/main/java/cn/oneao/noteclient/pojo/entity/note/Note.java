package cn.oneao.noteclient.pojo.entity.note;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("note")
public class Note {
    @TableId(type = IdType.AUTO)
    private Integer id; //主键id
    private Integer userId;//用户id
    private String noteTitle;//笔记标题
    private String noteBody;//笔记小内容
    private String noteContent;//笔记全部内容
    private String noteTags;//笔记标签
    private Integer noteClassifyId;//笔记分类id
    @TableField(fill = FieldFill.INSERT)
    private Integer isTop;//是否置顶
    @TableField(fill = FieldFill.INSERT)
    private Integer isLock;//是否加锁
    private String lockPassword;//锁密码
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer isNewBuild;//是否新建 0 新建 1 非新建
    @TableField(fill = FieldFill.INSERT)
    private Integer isCollection;//(是否收藏默认不收藏)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;//创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;//更新时间
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;//是否删除
    private String noteBackgroundImage;//背景图片
}
