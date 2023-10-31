package cn.oneao.noteclient.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinDTO {
    private Integer page;
    private Integer pageSize;
    private String searchValue;
    private String classifyValue;
}
