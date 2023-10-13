package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.pojo.dto.SmallNoteTopStatusDTO;
import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.service.SmallNoteService;
import cn.oneao.noteclient.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("smallNote")
@Slf4j
public class SmallNoteController {
    @Autowired
    private SmallNoteService smallNoteService;

    /**
     * 获取当前用户的小记信息
     * @param userId 用户id
     * @return 返回小记列表
     */
    @GetMapping("/getInfo")
    public Result<Object> getSmallNoteInfo(@RequestParam("userId")Integer userId){
        List<SmallNoteVO> list = smallNoteService.getSmallNoteInfo(userId);
        return Result.success(list);
    }
    @PostMapping("/changeSmallNoteStatus")
    public Result<Object> changeSmallNoteStatus(@RequestBody SmallNoteTopStatusDTO smallNoteTopStatusDTO){
        smallNoteService.changeSmallNoteStatus(smallNoteTopStatusDTO);
        return Result.success(ResponseEnums.SmallNote_UPDATE_STATUS_SUCCESS);
    }
}
