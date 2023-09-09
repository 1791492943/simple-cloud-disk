package com.simpleclouddisk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("project")
@Data
public class ProjectConfig {

    public static Long space;

    public void setSpace(Long space) {
        ProjectConfig.space = space;
    }

}
