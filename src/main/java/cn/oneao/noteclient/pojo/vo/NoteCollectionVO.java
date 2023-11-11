package cn.oneao.noteclient.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteCollectionVO {
    private Integer id; //笔记id
    private Integer serialNumber;//序号 1,2,3,4,5
    private String title;//标题
    private String tags;//标签
    private String content;//内容
    private Date operationTime;//操作时间
}
