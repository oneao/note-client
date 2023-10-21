package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.pojo.dto.*;
import cn.oneao.noteclient.pojo.vo.SmallNoteOneVO;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.service.SmallNoteService;
import cn.oneao.noteclient.utils.ResponseUtils.PageResult;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("smallNote")
@Slf4j
public class SmallNoteController {
    @Autowired
    private SmallNoteService smallNoteService;

    /**
     * 获取当前用户的小记信息
     * @param smallNotePageDTO 用户id
     * @return 返回小记列表
     */
    @PostMapping("/getInfo")
    public Result<Object> getSmallNoteInfo(@RequestBody SmallNotePageDTO smallNotePageDTO){
        PageResult<SmallNoteVO> list = smallNoteService.getSmallNoteInfo(smallNotePageDTO);
        return Result.success(list);
    }

    /**
     * 改变小记的置顶状态
     * @param smallNoteTopStatusDTO : 小记状态对象
     * @return 返回成功信息
     */
    @PostMapping("/changeSmallNoteStatus")
    public Result<Object> changeSmallNoteStatus(@RequestBody SmallNoteTopStatusDTO smallNoteTopStatusDTO){
        smallNoteService.changeSmallNoteStatus(smallNoteTopStatusDTO);
        return Result.success(ResponseEnums.SmallNote_UPDATE_STATUS_SUCCESS);
    }

    /**
     * 删除小记
     * @param smallNoteDeleteDTO 前端传来的对象
     * @return 返回删除成功的信息
     */
    @DeleteMapping("/deleteSmallNote")
    public Result<Object> deleteSmallNote(@RequestBody SmallNoteDeleteDTO smallNoteDeleteDTO){
        smallNoteService.deleteSmallNote(smallNoteDeleteDTO);
        return Result.success(ResponseEnums.SmallNote_DELETE_LOGIC_SUCCESS);
    }

    /**
     * 新增小记
     * @param smallNoteAddDTO 新增小记对象
     * @return 返回成功信息
     */
    @PostMapping("/addSmallNote")
    public Result<Object> addSmallNote(@RequestBody SmallNoteAddDTO smallNoteAddDTO){
        smallNoteService.addSmallNote(smallNoteAddDTO);
        return Result.success(ResponseEnums.SmallNote_ADD_SUCCESS);
    }

    /**
     * 获取单个小记
     * @param smallNoteId 小记id
     * @return 返回该小记的信息
     */
    @GetMapping("/getOneSmallNote")
    public Result<Object> getOneSmallNote(@RequestParam("id")Integer smallNoteId){
        SmallNoteOneVO oneSmallNote = smallNoteService.getOneSmallNote(smallNoteId);
        return Result.success(oneSmallNote);
    }

    /**
     * 更新小记
     * @param smallNoteUpdateDTO 前端填的信息
     * @return 返回成功的信息
     */
    @PostMapping("/updateSmallNote")
    public Result<Object> updateSmallNote(@RequestBody SmallNoteUpdateDTO smallNoteUpdateDTO){
        smallNoteService.updateSmallNote(smallNoteUpdateDTO);
        return Result.success(ResponseEnums.SmallNote_UPDATE_STATUS_SUCCESS);
    }
}
