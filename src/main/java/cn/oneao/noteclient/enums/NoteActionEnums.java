package cn.oneao.noteclient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoteActionEnums {
    DELETE_SMALL_NOTE_LOGIC("LOGIC_DELETE_SMALL_NOTE","用户逻辑删除小记"),
    DELETE_SMALL_NOTE_COMPLETE("COMPLETE_DELETE_SMALL_NOTE","用户彻底删除小记"),
    USER_ADD_SMALL_NOTE("USER_ADD_SMALL_NOTE","用户添加小记"),
    USER_UPDATE_SmallNote("USER_UPDATE_SMALL_NOTE","用户更新了小记"),
    SYSTEM_LOGIN_DELETE_SmallNote("SYSTEM_LOGIN_DELETE_SMALL_NOTE","系统逻辑删除小记");
    private final String actionName;
    private final String actionDesc;
}
