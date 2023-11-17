package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.note.NoteShare;
import cn.oneao.noteclient.pojo.vo.NoteShareAllVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteShareMapper extends BaseMapper<NoteShare> {
    Integer getUserIdByNoteShareId(@Param("noteShareId") Integer noteShareId);

    List<NoteShareAllVO> getShareNoteAll(@Param("userId") int userId,
                                         @Param("title") String title,
                                         @Param("day") Integer day,
                                         @Param("isExpire") Integer isExpire,
                                         @Param("page")Integer page,
                                         @Param("pageSize")Integer pageSize);
    Integer getShareNoteAllTotal(@Param("userId") int userId,
                                 @Param("title") String title,
                                 @Param("day") Integer day,
                                 @Param("isExpire") Integer isExpire
                              );
    Integer getShareNoteCommentNumber(@Param("noteShareId") Integer noteShareId);
}
