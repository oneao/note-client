package cn.oneao.noteclient.pojo.vo;

import cn.oneao.noteclient.pojo.entity.RecycleBin;
import lombok.Data;

import java.util.List;
@Data
public class RecycleBinVO {
    private Integer total;
    private List<RecycleBin> list;
}
