package com.elijah.dubborouterconsumer;

import com.elijah.dubboroutercommon.DubboRouterService;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterconsumer
 * Date: 2019-08-03 10:34
 */
@Service
public class Consume {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference(loadbalance = "grey")
    DubboRouterService dubboRouterService;

    @PostConstruct
    public void doConsume() {
        logger.info("------------------select version:{}", dubboRouterService.selectVersion());
    }
}
