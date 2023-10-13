package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.pojo.vo.SmallNoteVO;
import cn.oneao.noteclient.service.SmallNoteService;
import cn.oneao.noteclient.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("smallNote")
@Slf4j
public class SmallNoteController {
    @Autowired
    private SmallNoteService smallNoteService;
    @GetMapping("/getInfo")
    public Result<Object> getSmallNoteInfo(@RequestParam("userId")Integer userId){
        List<SmallNoteVO> list = smallNoteService.getSmallNoteInfo(userId);
        return Result.success(list);
    }
}
