package cn.oneao.noteclient.pojo.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class NoteShareLickDTO {
    private Integer id;//分享表id
    private Integer likeHeart;//true:1点赞 false:0取消点赞
}
