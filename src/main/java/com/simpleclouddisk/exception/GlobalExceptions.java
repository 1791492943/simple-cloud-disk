package com.simpleclouddisk.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.exception.service.SpaceException;
import com.simpleclouddisk.utils.HttpCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(SpaceException.class)
    public Result spaceException(SpaceException e) {
        return Result.error(e.getMessage(),HttpCode.SPACE_OVERFLOW);
    }

    @ExceptionHandler(ServiceException.class)
    public Result serviceException(ServiceException e){
        return Result.error(e.getMessage(), HttpCode.SERVICE_ERROR);
    }

    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e){
        e.printStackTrace();
        return Result.error(e.getMessage(), HttpCode.ERROR);
    }

}
