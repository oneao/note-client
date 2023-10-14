package cn.oneao.noteclient.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoteActionEnums {
    DELETE_SMALL_NOTE_LOGIC("LOGIC_DELETE_SMALL_NOTE","用户逻辑删除小记"),
    DELETE_SMALL_NOTE_COMPLETE("COMPLETE_DELETE_SMALL_NOTE","用户彻底删除小记");
    private final String actionName;
    private final String actionDesc;
}
