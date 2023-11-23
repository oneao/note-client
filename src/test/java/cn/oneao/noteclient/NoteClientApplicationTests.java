package cn.oneao.noteclient;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


@SpringBootTest(classes = NoteClientApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class NoteClientApplicationTests {
    @Autowired
    private ElasticsearchClient client;

    @Test
    public void createTest() throws IOException {
        CreateIndexResponse indexResponse = client.indices().create(c -> c.index("user"));
    }
    @Test
    public void queryTest() throws IOException {
        GetIndexResponse getIndexResponse = client.indices().get(i -> i.index("user"));
    }
    @Test
    public void existsTest() throws IOException {
        BooleanResponse booleanResponse = client.indices().exists(e -> e.index("user"));
        System.out.println(booleanResponse.value());
    }
    @Test
    public void deleteTest() throws IOException {
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(d -> d.index("user"));
        System.out.println(deleteIndexResponse.acknowledged());
    }
}
