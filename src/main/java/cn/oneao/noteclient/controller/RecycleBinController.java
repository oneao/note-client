package cn.oneao.noteclient.controller;

import cn.oneao.noteclient.pojo.dto.RecycleBinDTO;
import cn.oneao.noteclient.pojo.dto.RecycleBinManyDTO;
import cn.oneao.noteclient.pojo.dto.RecycleBinRecoverDTO;
import cn.oneao.noteclient.service.RecycleBinService;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycle")
@Slf4j
public class RecycleBinController {
    @Autowired
    private RecycleBinService recycleBinService;
    @PostMapping("/getRecycleBin")
    public Result<Object> getRecycleBin(@RequestBody RecycleBinDTO recycleBinDTO){
        return recycleBinService.getRecycleBin(recycleBinDTO);
    }
    @PutMapping("/recoverOneRecord")
    public Result<Object> recoverOneRecord(@RequestBody RecycleBinRecoverDTO recycleBinRecoverDTO){
        return recycleBinService.recoverOneRecord(recycleBinRecoverDTO);
    }
    @DeleteMapping("/deleteMany")
    public Result<Object> deleteMany(@RequestBody RecycleBinManyDTO recycleBinManyDTO){
        return recycleBinService.deleteMany(recycleBinManyDTO);
    }
    @PutMapping("/recoverMany")
    public Result<Object> recoverMany(@RequestBody RecycleBinManyDTO recycleBinManyDTO){
        return recycleBinService.recoverMany(recycleBinManyDTO);
    }
}
