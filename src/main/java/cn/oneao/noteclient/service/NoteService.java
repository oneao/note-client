package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.note.*;
import cn.oneao.noteclient.pojo.entity.note.Note;
import cn.oneao.noteclient.pojo.vo.NoteVO;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface NoteService extends IService<Note> {
    //获取笔记列表信息
    List<NoteVO> getNoteInfo();
    //验证用户存储的笔记密码
    Boolean  verifyNoteLockPassword(NoteLockPassWordDTO noteVerifyLockPassWordDTO);
    //修改笔记的置顶状态
    void changeNoteTopStatus(NoteTopStatusDTO noteTopStatusDTO);
    //删除笔记状态
    Result<Object> deleteNote(NoteDeleteDTO noteDeleteDTO);
    //新增笔记
    Result<Integer> addNote();
    //获取单个笔记信息
    Result<Object> getNoteById(Integer noteId);
    //为笔记添加锁
    Result<Object> addNoteLockPassword(NoteLockPassWordDTO noteLockPassWordDTO);
    //彻底删除笔记密码
    Result<Object> completelyLiftedNoteLockPassword(NoteLockPassWordDTO noteLockPassWordDTO);
    //移除redis中的key
    Result<Object> removeAccessToNote(Integer noteId);
    //更新笔记
    Result<Object> updateNoteContent(NoteUpdateContentDTO noteUpdateContentDTO);
    //更新笔记的收藏状态
    Result<Object> updateNoteCollection(NoteUpdateCollectionDTO updateCollectionDTO);

}
