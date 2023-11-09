package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.pojo.dto.note.NoteShareAddDTO;
import cn.oneao.noteclient.pojo.dto.note.NoteShareGetDTO;
import cn.oneao.noteclient.pojo.dto.note.NoteShareLickDTO;
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
}
