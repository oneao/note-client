package cn.oneao.noteclient.utils;

import cn.oneao.noteclient.pojo.entity.es.ESNote;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import jdk.dynalink.linker.LinkerServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ElasticSearchUtil {
    @Autowired
    private ElasticsearchClient client;

    /**
     * 索引是否存在
     *
     * @param indexName 索引名
     * @return 返回True或False
     */
    public boolean hasIndex(String indexName) throws IOException {
        BooleanResponse exists = client.indices().exists(d -> d.index(indexName));
        return exists.value();
    }

    /**
     * 删除索引
     *
     * @param indexName 索引名
     * @throws IOException io
     */
    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexResponse response = client.indices().delete(d -> d.index(indexName));
        return true;
    }

    /**
     * 创建索引
     *
     * @param indexName 索引名
     * @return 返回True或False
     */
    public boolean createIndex(String indexName) {
        try {
            CreateIndexResponse indexResponse = client.indices().create(c -> c.index(indexName));
        } catch (IOException e) {
            log.error("索引创建失败：{}", e.getMessage());
        }
        return true;
    }

    /**
     * 新增和更新数据
     *
     * @param indexName 索引名
     */
    public boolean insertOrUpdateDocument(String indexName, Object obj, String id) {
        try {
            IndexResponse indexResponse = client.index(i -> i
                    .index(indexName)
                    .id(id)
                    .document(obj));
            return true;
        } catch (IOException e) {
            log.error("数据插入ES异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除数据
     *
     * @param indexName 索引名
     * @param id        传入的id
     * @return 返回
     */
    public boolean deleteDocument(String indexName, String id) {
        try {
            DeleteResponse deleteResponse = client.delete(d -> d
                    .index(indexName)
                    .id(id)
            );
        } catch (IOException e) {
            log.error("删除Es数据异常：{}", e.getMessage());
        }
        return true;
    }



}
