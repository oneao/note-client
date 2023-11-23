package cn.oneao.noteclient.pojo.entity.es;

import cn.oneao.noteclient.constant.DocumentIndexConstant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
@Data
@Document(indexName = DocumentIndexConstant.NOTE_INDEX,createIndex = true)
public class ESNote implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @Field(type = FieldType.Integer,name = "userId")
    private Integer userId;//用户id
    @Field(type = FieldType.Integer,name = "noteId")
    private Integer noteId;//笔记id
    @Field(type = FieldType.Text,name = "title", analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;//笔记标题
    @Field(type = FieldType.Text,name = "content", analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;//笔记内容
    @Field(type = FieldType.Date,name = "updateTime",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;//更新时间
}
