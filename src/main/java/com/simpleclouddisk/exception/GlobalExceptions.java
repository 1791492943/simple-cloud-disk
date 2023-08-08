package com.simpleclouddisk.exception;

import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.utils.HttpCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(ServiceException.class)
    public Result serviceException(ServiceException e){
        return Result.error(e.getMessage(), HttpCode.SERVICE_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public Result exception(Exception e){
        e.printStackTrace();
        return Result.error("未知异常,请联系管理员!!", HttpCode.ERROR);
    }

}
