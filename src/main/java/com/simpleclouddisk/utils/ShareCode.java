package com.simpleclouddisk.utils;

import java.util.Random;

public class ShareCode {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String getCode() {
        StringBuilder code = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 4; i++) { // 生成4位长度的验证码
            int index = random.nextInt(CHARACTERS.length());
            char character = CHARACTERS.charAt(index);
            code.append(character);
        }

        return code.toString();
    }
}