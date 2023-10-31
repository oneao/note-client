package cn.oneao.noteclient.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBin {
    private Integer id; //笔记id和小记id
    private Integer type;//类别 0 1
    private String typeName;//类别名字 0小记 1笔记
    private Integer serialNumber;//序号 1,2,3,4,5
    private String title;//标题
    private String tags;//标签
    private Date operationTime;//操作时间
}
