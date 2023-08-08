package com.simpleclouddisk.utils;

import com.simpleclouddisk.config.CodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class CaptchaGenerator {


    public static String code() {
        String characters = "0123456789";
        StringBuilder captcha = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < CodeConfig.CODE_LENGTH; i++) {
            int index = random.nextInt(characters.length());
            captcha.append(characters.charAt(index));
        }
        log.info("短信验证码: {}", captcha);
        return captcha.toString();
    }
}
