package com.elijah.dubborouterconsumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DubborouterconsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubborouterconsumerApplication.class, args);
    }

}
