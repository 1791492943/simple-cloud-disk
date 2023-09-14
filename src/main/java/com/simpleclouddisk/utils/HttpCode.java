package com.simpleclouddisk.utils;

public class HttpCode {

    public static final Integer SUCCESS = 200; // 成功
    public static final Integer SERVICE_ERROR = 500; // 业务异常
    public static final Integer ERROR = 510; // 未知异常
    public static final Integer SPACE_OVERFLOW = 501; // 使用空间溢出,需要扩容
    public static final Integer LOGIN_FAIL = 502; // 登陆失败
}
