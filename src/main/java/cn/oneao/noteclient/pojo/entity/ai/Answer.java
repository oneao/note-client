package cn.oneao.noteclient.pojo.entity.ai;

import lombok.Data;

import java.util.List;

@Data
public class Answer {
    private String id;
    private String object;
    private String model;
    private Integer created;
    private List<Choices> choices;
}