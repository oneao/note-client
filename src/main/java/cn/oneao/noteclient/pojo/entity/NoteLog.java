package cn.oneao.noteclient.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@TableName("note_log")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String action;//操作
    private String actionDesc;//操作描述
    private Integer userId;//操作用户id
    private Integer noteId;//笔记id
    private Integer smallNoteId;//小记id
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;//创建时间
}
