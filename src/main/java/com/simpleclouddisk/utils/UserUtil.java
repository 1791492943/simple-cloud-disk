package com.simpleclouddisk.utils;

import cn.dev33.satoken.stp.StpUtil;

public class UserUtil {

    public static Long getUserId(){
        return StpUtil.getLoginIdAsLong();
    }

}
