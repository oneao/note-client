package cn.oneao.noteclient;

import cn.oneao.noteclient.config.SSLCertificateIgnoreConfig;
import io.github.asleepyfish.annotation.EnableChatGPT;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //开启定时任务
@EnableChatGPT  //GPT
public class NoteClientApplication {
    public static void main(String[] args) {
        SSLCertificateIgnoreConfig.ignoreSSLCertificate();
        SpringApplication.run(NoteClientApplication.class, args);
    }
}
