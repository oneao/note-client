package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NoteShareAllVO {
    private Integer noteShareId;//笔记id
    private Integer serialNumber;//序号
    private String title;//标题
    private String tags;//标签
    private String remark;//笔记备注
    private String content;//内容
    private String shareLink;//分享链接
    private Integer likeNumber;//点赞数
    private Integer commentNumber;//评论数
    private Integer visitNumber;//浏览量
    private Date shareTime;//分享时间
    private Integer shareDay;//分享天数
    private Integer isShareExpire;//是否过期
    private Integer isNeedPassword;//是否需要密码
}
