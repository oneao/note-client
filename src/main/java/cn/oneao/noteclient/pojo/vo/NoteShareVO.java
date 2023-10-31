package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteShareVO {
    private String nickName;//分享人昵称
    private Integer noteShareTime;//分享有效时间
    private String noteShareTitle;//分享标题
    private String noteShareContent;//分享笔记内容
    private String noteShareTags;//标签
    private String noteShareRemark;//备注
    private Integer noteLikeNumber;//该笔记的点赞数量
    private Date noteShareThatTime;//分享的当时时间
    private Integer noteShareVisitNumber;//访问量
    private Integer isLike;//该ip是否点赞
}
