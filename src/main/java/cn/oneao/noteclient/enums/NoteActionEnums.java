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
    SYSTEM_LOGIN_DELETE_SmallNote("SYSTEM_LOGIN_DELETE_SMALL_NOTE","系统逻辑删除小记"),
    DELETE_NOTE_LOGIC("LOGIC_DELETE_NOTE","用户逻辑删除笔记"),
    DELETE_NOTE_COMPLETE("COMPLETE_DELETE_NOTE","用户彻底删除笔记"),
    USER_ADD_NOTE("USER_ADD_NOTE","用户添加笔记"),
    USER_UPDATE_NOTE("USER_UPDATE_NOTE","用户更新笔记"),
    USER_SHARE_NOTE("USER_SHARE_NOTE","用户分享笔记"),
    USER_RECOVER_SmallNote("USER_RECOVER_SMALL_NOTE","用户恢复小记"),
    USER_RECOVER_Note("USER_RECOVER_NOTE","用户恢复笔记"),
    DELETE_NOTE_MANY("DELETE_NOTE_MANY","批量删除"),
    DELETE_SMALL_NOTE_MANY("DELETE_SMALL_NOTE_MANY","批量删除"),
    RECOVER_NOTE_MANY("RECOVER_NOTE_MANY","批量恢复"),
    RECOVER_SMALL_NOTE_MANY("RECOVER_SMALL_NOTE_MANY","批量恢复");
    private final String actionName;
    private final String actionDesc;
}
