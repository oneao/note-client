package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.pojo.dto.note.*;
import cn.oneao.noteclient.service.NoteShareService;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/noteShare")
@RestController
@Slf4j
public class NoteShareController {
    @Autowired
    private NoteShareService noteShareService;

    /**
     * 新增分享笔记
     * @param noteShareAddDTO   新增分享笔记的内容
     * @return 返回分享地址
     */
    @PostMapping("/addNoteShare")
    public Result<Object> addNoteShare(@RequestBody NoteShareAddDTO noteShareAddDTO){
        return noteShareService.addNoteShare(noteShareAddDTO);
    }
    /**
     * 判断是否笔记是否已分享
     * @param noteId 笔记id
     * @return 返回
     */
    @GetMapping("/getNoteIsShare")
    public Result<Object> getNoteIsShare(@RequestParam("noteId")Integer noteId){
        return noteShareService.getNoteIsShare(noteId);
    }
    /**
     * 检查该分享的笔记是否有锁或者是否存在.
     * @param n_sid 分享表中的主键id
     * @return 返回响应码，判断是否存在锁和存在该分享笔记
     */
    @GetMapping("/getShareNoteIsLock")
    public Result<Object> getShareNoteIsLock(@RequestParam("n_sid")Integer n_sid){
        return noteShareService.getShareNoteIsLock(n_sid);
    }
    /**
     * 根据分享表中的主键id获取笔记分享的信息
     * @param noteShareGetDTO 包含 分享表中的主键id 和 访问密码，注：访问密码可能为空
     * @return 返回笔记分享的信息
     */
    @PostMapping("/getShareNote")
    public Result<Object> getShareNote(@RequestBody NoteShareGetDTO noteShareGetDTO, HttpServletRequest httpServletRequest){
        return noteShareService.getShareNote(noteShareGetDTO,httpServletRequest);
    }
    /**
     * 点赞
     * @param noteShareLickDTO 点赞
     * @return 返回
     */
    @PutMapping("/goToLick")
    public Result<Object> goToLick(@RequestBody NoteShareLickDTO noteShareLickDTO, HttpServletRequest httpServletRequest){
        return noteShareService.goToLick(noteShareLickDTO,httpServletRequest);
    }
    /**
     * 获取分享笔记的列表
     * @param noteShareSearchDTO 前端传来的DTO
     * @return 返回
     */
    @PostMapping("/getShareNoteAll")
    public Result<Object> getShareNoteAll(@RequestBody NoteShareSearchDTO noteShareSearchDTO){
        return noteShareService.getShareNoteAll(noteShareSearchDTO);
    }
    /**
     * 取消分享一个笔记
     * @param noteShareId 分享笔记id
     * @return 返回
     */
    @PatchMapping("/cancelShareNote/{noteShareId}")
    public Result<Object> cancelShareNote(@PathVariable("noteShareId") Integer noteShareId){
        return noteShareService.cancelShareNote(noteShareId);
    }
    /**
     * 更新分享笔记天数
     * @param noteShareUpdateShareDayDTO 分享笔记天数对象
     * @return 返回
     */
    @PostMapping("/updateShareNoteDay")
    public Result<Object> updateShareNoteDay(@RequestBody NoteShareUpdateShareDayDTO noteShareUpdateShareDayDTO){
        return noteShareService.updateShareNoteDay(noteShareUpdateShareDayDTO);
    }

    /**
     * 为分享笔记添加访问密码
     * @param noteShareLockDTO 访问笔记id和密码
     * @return 返回成功
     */
    @PostMapping("/addShareNoteLock")
    public Result<Object> addShareNoteLock(@RequestBody NoteShareLockDTO noteShareLockDTO){
        return noteShareService.addShareNoteLock(noteShareLockDTO);
    }
    /**
     * 为分享笔记删除访问密码
     * @param noteShareLockDTO 访问笔记id和密码
     * @return 返回成功
     */
    @PostMapping("/delShareNoteLock")
    public Result<Object> delShareNoteLock(@RequestBody NoteShareLockDTO noteShareLockDTO){
        return noteShareService.delShareNoteLock(noteShareLockDTO);
    }

    /**
     * 更新分享笔记的内容：标题，标签，备注。
     * @param noteShareUpdateContentDTO 标题，标签，备注。
     * @return 返回修改成功
     */
    @PostMapping("/updateShareNoteContent")
    public Result<Object> updateShareNoteContent(@RequestBody NoteShareUpdateContentDTO noteShareUpdateContentDTO){
        return noteShareService.updateShareNoteContent(noteShareUpdateContentDTO);
    }
}
