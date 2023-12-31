package cn.oneao.noteclient.interceptor;

import cn.oneao.noteclient.enums.ResponseEnums;
import cn.oneao.noteclient.service.UserService;
import cn.oneao.noteclient.utils.GlobalObjectUtils.UserContext;
import cn.oneao.noteclient.utils.JwtHelper;
import cn.oneao.noteclient.utils.ResponseUtils.Result;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Map;
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //OPTIONS方法是HTTP协议中定义的一种请求方法。它用于在客户端和服务器之间发起一个预检请求（Preflight Request），以确定实际的请求是否被服务器接受。
        String method = request.getMethod();
        if("OPTIONS".equalsIgnoreCase(method)){
            return true;
        }
        if (request.getRequestURI().equals("/note/error")){
            Result<Object> result = Result.error(ResponseEnums.UNKNOWN_ERROR);
            String jsonString = JSONObject.toJSONString(result);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println(jsonString);
            out.flush();
            out.close();
            return false;
        }
        boolean flag = false;
        try {
            String userId = request.getHeader("id");
            String token = request.getHeader("Authorization");
            if(!ObjectUtils.isEmpty(token) && token.length() > 11){
                token = token.substring(7);
                //如果验证通过,即为通过。
                JwtHelper.verify(token);
                //获取token中的信息
                Map<String, Claim> userInfo = JwtHelper.getTokenInfo(token);
                Claim claim = userInfo.get("userId");
                Integer jwtUserId = claim.asInt();
                if(userId.equals(String.valueOf(jwtUserId))){
                    UserContext.setUserId(Integer.parseInt(userId));
                    flag = true;
                }
            }
        }catch (Exception e){
            flag = false;
            e.printStackTrace();
        }
        if(!flag){
            //返回json数据给前端
            Result<Object> result = Result.error(ResponseEnums.USER_TOKEN_ERR);
            String jsonString = JSONObject.toJSONString(result);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println(jsonString);
            out.flush();
            out.close();
        }
        return flag;
    }
}