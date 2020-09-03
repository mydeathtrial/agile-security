package com.agile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author 佟盟
 * 日期 2020/8/00024 16:23
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@EnableCaching
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class App {
    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
    }
}
