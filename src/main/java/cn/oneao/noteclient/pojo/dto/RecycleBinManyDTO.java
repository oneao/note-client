package cn.oneao.noteclient.pojo.dto;

import lombok.Data;

import java.util.List;
@Data
public class RecycleBinManyDTO {
    private List<Integer> types;
    private List<Integer> ids;
}
