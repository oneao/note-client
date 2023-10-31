package cn.oneao.noteclient.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.net.ssl.SSLHandshakeException;
import java.io.EOFException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SSLHandshakeException.class)
    public void businessExceptionHandler(SSLHandshakeException e){
        log.error("发生异常类型为SSL连接异常，产生原因：调用Gpt接口所致!");
    }
    @ExceptionHandler(value = EOFException.class)
    public void businessExceptionHandler(EOFException e){
        log.error("发生异常类型为SSL连接异常，产生原因：调用Gpt接口所致!");
    }
}
