package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.note.*;
import cn.oneao.noteclient.pojo.entity.note.NoteShare;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;


public interface NoteShareService extends IService<NoteShare> {
    //检查笔记是否分享过了
    Result<Object> getNoteIsShare(Integer noteId);
    //保存分享笔记
    Result<Object> addNoteShare(NoteShareAddDTO noteShareAddDTO);
    //获取分享笔记的内容
    Result<Object> getShareNote(NoteShareGetDTO noteShareGetDTO, HttpServletRequest httpServletRequest);
    //检查笔记是否需要锁
    Result<Object> getShareNoteIsLock(Integer n_sid);
    //点赞或取消点赞
    Result<Object> goToLick(NoteShareLickDTO noteShareLickDTO, HttpServletRequest httpServletRequest);
    //获取分享笔记列表
    Result<Object> getShareNoteAll(NoteShareSearchDTO noteShareSearchDTO);
    //取消分享笔记
    Result<Object> cancelShareNote(Integer noteShareId);
    //修改笔记分享天数
    Result<Object> updateShareNoteDay(NoteShareUpdateShareDayDTO noteShareUpdateShareDayDTO);
    //为分享笔记添加访问密码
    Result<Object> addShareNoteLock(NoteShareLockDTO noteShareLockDTO);
    //为分享笔记删除访问密码
    Result<Object> delShareNoteLock(NoteShareLockDTO noteShareLockDTO);
    //更新分享笔记的内容：标题，标签，备注。
    Result<Object> updateShareNoteContent(NoteShareUpdateContentDTO noteShareUpdateContentDTO);
}
