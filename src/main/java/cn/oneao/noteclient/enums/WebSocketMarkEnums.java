package cn.oneao.noteclient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebSocketMarkEnums {
    COMMENT_REPLY_NOTICE("001"),
    USER_LEVEL_UP("002");
    private final String mark;
}
