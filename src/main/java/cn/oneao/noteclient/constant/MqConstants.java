package cn.oneao.noteclient.constant;

public class MqConstants {
    //以下是用于数据库和es保持数据一致性。
    //交换机
    public final static String NOTE_EXCHANGE = "note.topic";
    //监听新增和修改的队列
    public final static String NOTE_INSERT_OR_UPDATE_QUEUE = "note.insert.or.update.queue";
    //监听删除的队列
    public final static String NOTE_DELETE_QUEUE = "note.delete.queue";
    //新增或修改的RoutingKey
    public final static String NOTE_INSERT_OR_UPDATE_KEY = "note.insert.or.update";
    //删除的RoutingKey
    public final static String NOTE_DELETE_KEY = "note.delete";
}
