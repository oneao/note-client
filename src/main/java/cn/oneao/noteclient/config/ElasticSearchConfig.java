package cn.oneao.noteclient.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    @Value("${elasticsearch.host}")
    private String host;
    @Value("${elasticsearch.port}")
    private Integer port;
    @Value("${elasticsearch.connect}")
    private String connect;

    //注入IOC容器
    //@Bean
    //public ElasticsearchClient elasticsearchClient(){
    //    RestClient client = RestClient.builder(new HttpHost(host,port,connect)).build();
    //    ElasticsearchTransport transport = new RestClientTransport(client,new JacksonJsonpMapper());
    //    return new ElasticsearchClient(transport);
    //}
}
