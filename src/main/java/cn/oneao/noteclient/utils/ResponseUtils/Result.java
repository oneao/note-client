package cn.oneao.noteclient.utils.ResponseUtils;

import cn.oneao.noteclient.enums.ResponseEnums;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code; //编码
    private String message; //信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(T obj,ResponseEnums responseEnums) {
        Result<T> result = new Result<T>();
        result.code = responseEnums.getCode();
        result.message = responseEnums.getMsg();
        result.data = obj;
        return result;
    }
    public static <T> Result<T> success(ResponseEnums responseEnums) {
        Result<T> result = new Result<T>();
        result.code = responseEnums.getCode();
        result.message = responseEnums.getMsg();
        return result;
    }
    public static <T> Result<T> error(ResponseEnums responseEnums) {
        Result<T> result = new Result<T>();
        result.code = responseEnums.getCode();
        result.message = responseEnums.getMsg();
        return result;
    }
    //msg也可以自定义
    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.message = msg;
        result.code = 0;
        return result;
    }

    public static <T> Result<T> error(Integer code,String msg) {
        Result result = new Result();
        result.message = msg;
        result.code = code;
        return result;
    }
}
