package com.simpleclouddisk.common;

import com.simpleclouddisk.utils.HttpCode;
import lombok.Data;

@Data
public class Result<T> {

    private T data;
    private Integer code;
    private String msg;

    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(HttpCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static Result error(String msg, Integer code) {
        Result<Object> result = new Result<>();
        result.setMsg(msg);
        result.setCode(code);
        return result;
    }
}
