package com.elijah.dubborouterconsumer;

import com.elijah.dubboroutercommon.DubboRouterService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDubbo
public class DubborouterconsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubborouterconsumerApplication.class, args);
    }

}
