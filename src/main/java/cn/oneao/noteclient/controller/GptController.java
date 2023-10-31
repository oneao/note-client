package cn.oneao.noteclient.controller;

import io.github.asleepyfish.util.OpenAiUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLHandshakeException;
import java.io.EOFException;
import java.io.IOException;
@RequestMapping("/openai")
@RestController
@Slf4j
public class GptController {
    @GetMapping
    public void streamChatWithWeb(String content, HttpServletResponse response) throws IOException {
        try {
            // 需要指定response的ContentType为流式输出，且字符编码为UTF-8
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            OpenAiUtils.createStreamChatCompletion(content, response.getOutputStream());
        }catch (SSLHandshakeException | EOFException s){
            log.error("发生异常类型为SSL连接异常，产生原因：调用Gpt接口所致!");
        }
    }
}
