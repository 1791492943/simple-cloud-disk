package com.simpleclouddisk.utils;

public class RedisUtil {
    /**
     * 手机号登录前置标识
     */
    public static final String PHONE_PREFIX = "phone-code: ";

    /**
     * 手机号登录前置标识
     */
    public static String redisPhoneCode(String phone){
        return PHONE_PREFIX + phone;
    }

    /**
     * 密码错误前置标识
     */
    public static final String PASSWORD_PREFIX = "password-count: ";

    /**
     * 密码错误前置标识
     */
    public static String redisPasswordCount(long userId) {
        return PHONE_PREFIX + userId;
    }

}
