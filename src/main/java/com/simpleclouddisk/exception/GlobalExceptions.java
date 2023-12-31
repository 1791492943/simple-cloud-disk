package com.simpleclouddisk.exception;

import com.simpleclouddisk.common.Result;
import com.simpleclouddisk.exception.service.LoginException;
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

    @ExceptionHandler(LoginException.class)
    public Result loginException(LoginException e){
        Result result = new Result();
        result.setMsg(e.getMessage());
        result.setCode(HttpCode.LOGIN_FAIL);
        return result;
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
