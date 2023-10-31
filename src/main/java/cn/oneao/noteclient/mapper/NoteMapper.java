package cn.oneao.noteclient.mapper;

import cn.oneao.noteclient.pojo.entity.note.Note;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    void completeDeleteNote(@Param("noteId") Integer noteId);

    void completeDeleteNewCreateNote(@Param("noteId") Integer noteId);

    void updateNoteRecoveryOne(@Param("noteId")Integer noteId);

    void completeDeleteNotes(@Param("noteIds") List<Integer> noteIds);

    void recoverMany(@Param("noteIds") List<Integer> noteIds);
}
