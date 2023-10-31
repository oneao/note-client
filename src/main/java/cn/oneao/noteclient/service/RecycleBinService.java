package cn.oneao.noteclient.service;

import cn.oneao.noteclient.pojo.dto.RecycleBinDTO;
import cn.oneao.noteclient.pojo.dto.RecycleBinManyDTO;
import cn.oneao.noteclient.pojo.dto.RecycleBinRecoverDTO;
import cn.oneao.noteclient.pojo.entity.RecycleBin;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RecycleBinService extends IService<RecycleBin> {
    Result<Object> getRecycleBin(RecycleBinDTO recycleBinDTO);
    //恢复一个记录
    Result<Object> recoverOneRecord(RecycleBinRecoverDTO recycleBinRecoverDTO);
    //批量删除
    Result<Object> deleteMany(RecycleBinManyDTO recycleBinManyDTO);
    //批量恢复
    Result<Object> recoverMany(RecycleBinManyDTO recycleBinManyDTO);
}
