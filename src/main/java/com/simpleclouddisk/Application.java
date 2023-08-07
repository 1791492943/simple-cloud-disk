package com.simpleclouddisk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication
@EnableScheduling
@Slf4j
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
        log.info("项目启动成功");
    }
}
