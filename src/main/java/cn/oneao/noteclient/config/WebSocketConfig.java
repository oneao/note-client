package cn.oneao.noteclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@Component
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        // 创建 ServerEndpointExporter 实例
        ServerEndpointExporter exporter = new ServerEndpointExporter();

        // 返回 ServerEndpointExporter 实例
        return exporter;
    }

}