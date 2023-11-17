package cn.oneao.noteclient.pojo.entity.rabbitmq;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RMUserLevelUpNotice implements Serializable {
    private static final long serialVersionUID = 1L;
    private String mark;//标记字段
    private Integer userId;
    private Integer level;
}
