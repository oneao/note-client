package cn.oneao.noteclient.interceptor;

import cn.oneao.noteclient.pojo.entity.log.SqlActionLog;
import cn.oneao.noteclient.utils.GlobalObjectUtils.GlobalObject;
import cn.oneao.noteclient.utils.GlobalObjectUtils.GlobalObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.*;

@Slf4j
@RestControllerAdvice
public class RequestBodyInterceptor implements RequestBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        GlobalObject object = GlobalObjectUtil.getInstance().getObject();
        SqlActionLog sqlActionLog = object.getSqlActionLog();
        //类名
        String className = parameter.getDeclaringClass().getName();
        sqlActionLog.setRequestClass(className);
        //请求路径:类上
        RequestMapping classRequestMapping = AnnotationUtils.findAnnotation(parameter.getContainingClass(), RequestMapping.class);
        //请求路径:方法上
        RequestMapping methodRequestMapping = parameter.getMethodAnnotation(RequestMapping.class);
        //获取请求路径
        StringBuilder requestMapping = getStringBuilder(classRequestMapping, methodRequestMapping);
        sqlActionLog.setRequestMapping(requestMapping.toString());
        //请求的方法
        if(parameter.getMethod() != null){
            String methodSignature = getMethodSignature(parameter.getMethod());
            sqlActionLog.setRequestMethod(methodSignature);
        }
        return inputMessage;
    }

    private static StringBuilder getStringBuilder(RequestMapping classRequestMapping, RequestMapping methodRequestMapping) {
        StringBuilder requestMapping = new StringBuilder();
        if (classRequestMapping != null) {
            String[] classMappingValue = classRequestMapping.value();
            if (classMappingValue[0].charAt(0) != '/'){
                requestMapping.append("/");
            }
            for (String s : classMappingValue){
                requestMapping.append(s);
            }
        }
        if (methodRequestMapping != null) {
            String[] methodMappingValue = methodRequestMapping.value();
            for (String s : methodMappingValue) {
                requestMapping.append(s);
            }
        }
        return requestMapping;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        GlobalObject object =GlobalObjectUtil.getInstance().getObject();
        SqlActionLog sqlActionLog = object.getSqlActionLog();
        String requestBody = body.toString();
        sqlActionLog.setRequestParam(requestBody);
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("请求体为空");
        return body;
    }
    private String getMethodSignature(Method method) {
        StringBuilder signatureBuilder = new StringBuilder();

        // 获取修饰符
        int modifiers = method.getModifiers();
        signatureBuilder.append(Modifier.toString(modifiers)).append(" ");

        // 获取返回类型
        Class<?> returnType = method.getReturnType();
        signatureBuilder.append(returnType.getSimpleName()).append(" ");

        // 获取方法名
        String methodName = method.getName();
        signatureBuilder.append(methodName).append("(");

        // 获取参数类型
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            signatureBuilder.append(parameterType.getSimpleName());
            if (i < parameters.length - 1) {
                signatureBuilder.append(", ");
            }
        }

        signatureBuilder.append(")");

        return signatureBuilder.toString();
    }
}
