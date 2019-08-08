package com.elijah.dubborouterconsumer;

import com.elijah.dubboroutercommon.DubboRouterService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterconsumer
 * Date: 2019-08-08 12:31
 */
@RestController
public class TestController{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    DubboRouterService dubboRouterService;

    private String doConsume(String bizzKey) {
        String resString = dubboRouterService.selectVersion(bizzKey);
        logger.info("------------------select version:{}", resString);
        return resString;
    }

    @GetMapping("/testDoGrayConsume")
    public String test(String bizzKey){
        return doConsume(bizzKey);
    }
}
