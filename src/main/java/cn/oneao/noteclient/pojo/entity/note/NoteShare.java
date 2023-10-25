package cn.oneao.noteclient.pojo.entity.note;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("note_share")
public class NoteShare {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer noteId;//笔记id
    private Integer isShare;//是否分享(0:不分享，1：分享)
    private Integer noteShareTime;//分享天数
    private String noteShareTitle;//分享标题
    private String noteShareTags;//分享标签
    private String noteShareRemark;//分享备注
    private Integer isNeedPassword;//是否需要笔记密码(0:不需要,1需要)
    private String noteSharePassword;//分享密码
    private Integer noteLikeNumber;//点赞数量
    private Date noteShareThatTime;//笔记分享的时间
    private Integer noteShareVisitNumber;//访问量
}
