package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.note.NoteShareAddDTO;
import cn.oneao.noteclient.pojo.dto.note.NoteShareGetDTO;
import cn.oneao.noteclient.pojo.dto.note.NoteShareLickDTO;
import cn.oneao.noteclient.pojo.entity.note.NoteShare;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface NoteShareService extends IService<NoteShare> {
    //检查笔记是否分享过了
    Result<Object> getNoteIsShare(Integer noteId);
    //保存分享笔记
    Result<Object> addNoteShare(NoteShareAddDTO noteShareAddDTO);
    //获取分享笔记的内容
    Result<Object> getShareNote(NoteShareGetDTO noteShareGetDTO);
    //检查笔记是否需要锁
    Result<Object> getShareNoteIsLock(Integer n_sid);
    //点赞或取消点赞
    Result<Object> goToLick(NoteShareLickDTO noteShareLickDTO);
}
