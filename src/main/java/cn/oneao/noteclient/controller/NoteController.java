package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.pojo.dto.note.*;
import cn.oneao.noteclient.pojo.vo.NoteVO;
import cn.oneao.noteclient.service.NoteService;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/note")
@RestController
@Slf4j
public class NoteController {
    @Autowired
    private NoteService noteService;
    /**
     * 获取当前登录用户的笔记信息
     * @return 返回笔记列表
     */
    @GetMapping("/getNoteInfo")
    public Result<Object> getNoteInfo(){
        //获取当前用户id
        List<NoteVO> noteInfo = noteService.getNoteInfo();

        return Result.success(noteInfo);
    }
    /** 验证笔记是否正确
     * @param noteVerifyLockPassWordDTO 验证密码对象
     * @return 返回验证成功或失败。
     */
    @PostMapping("/verifyNoteLockPassword")
    public Result<Object> verifyNoteLockPassword(@RequestBody NoteLockPassWordDTO noteVerifyLockPassWordDTO) {
        Boolean flag = noteService.verifyNoteLockPassword(noteVerifyLockPassWordDTO);
        if (flag) {
            //密码正确
            return Result.success(ResponseEnums.NOTE_VERIFY_LOCK_SUCCESS);
        }else{
            //密码错误
            return Result.success(ResponseEnums.NOTE_VERIFY_LOCK_ERROR);
        }
    }
    /**
     * 彻底删除笔记密码
     * @param noteLockPassWordDTO 验证密码对象
     * @return 返回验证成功或失败
     */
    @PutMapping("/completelyLiftedNoteLockPassword")
    public Result<Object> completelyLiftedNoteLockPassword(@RequestBody NoteLockPassWordDTO noteLockPassWordDTO){
        return noteService.completelyLiftedNoteLockPassword(noteLockPassWordDTO);
    }
    /**
     * 修改笔记的置顶状态
     * @param noteTopStatusDTO 笔记置顶对象
     * @return 返回修改成功
     */
    @PostMapping("/changeNoteTopStatus")
    public Result<Object> changeNoteTopStatus(@RequestBody NoteTopStatusDTO noteTopStatusDTO){
        noteService.changeNoteTopStatus(noteTopStatusDTO);
        return Result.success(ResponseEnums.NOTE_TOP_UPDATE_SUCCESS);
    }
    /**
     * 删除笔记
     * @param noteDeleteDTO 删除笔记对象
     * @return 响应删除成功与否
     */
    @DeleteMapping("/deleteNote")
    public Result<Object> deleteNote(@RequestBody NoteDeleteDTO noteDeleteDTO){
        return noteService.deleteNote(noteDeleteDTO);
    }
    /**
     * 添加笔记：逻辑添加笔记
     * @return 返回添加成功
     */
    @GetMapping("/addNote")
    public Result<Integer> addNote(){
        return noteService.addNote();
    }
    /**
     * 根据id获取单个笔记对象
     * @param noteId 笔记id
     * @return 返回一个笔记对象
     */
    @GetMapping("/getOneNote/{noteId}")
    public Result<Object> getNoteById(@PathVariable("noteId") Integer noteId){
        return noteService.getNoteById(noteId);
    }
    /**
     * 为笔记添加密码
     * @param noteLockPassWordDTO 笔记密码对象
     * @return 返回正确信息
     */
    @PostMapping("/addNoteLockPassword")
    public Result<Object> addNoteLockPassword(@RequestBody NoteLockPassWordDTO noteLockPassWordDTO){
        return noteService.addNoteLockPassword(noteLockPassWordDTO);
    }
    /**
     * 更新笔记内容
     * @param noteUpdateContentDTO 更新笔记内容体
     * @return 返回更新成功
     */
    @PutMapping("/updateNoteContent")
    public Result<Object> updateNoteContent(@RequestBody NoteUpdateContentDTO noteUpdateContentDTO){
        return noteService.updateNoteContent(noteUpdateContentDTO);
    }
    /**
     * 更新笔记的收藏状态
     * @param updateCollectionDTO 收藏笔记对象
     * @return 返回收藏成功与否
     */
    @PutMapping("/updateNoteCollection")
    public Result<Object> updateNoteCollection(@RequestBody NoteUpdateCollectionDTO updateCollectionDTO){
        return noteService.updateNoteCollection(updateCollectionDTO);
    }
    /**
     * 更新用户的笔记信息
     * @param noteUpdateMessageDTO 标题 主题 标签 背景图片
     * @return 返回
     */
    @PutMapping("/updateNoteMessage")
    public Result<Object> updateNoteMessage(@RequestBody NoteUpdateMessageDTO noteUpdateMessageDTO){
        return noteService.updateNoteMessage(noteUpdateMessageDTO);
    }
}
