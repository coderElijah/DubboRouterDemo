package com.elijah.dubbograystarter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterprovider.provider
 * Date: 2019-08-06 14:13
 */
public class GrayRouterApplicationStartListener implements ApplicationListener<ApplicationStartedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(GrayRouterApplicationStartListener.class);
    public GrayRouterApplicationStartListener() {
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        logger.info("DubboGrayRoute Init");
        //注册监听节点
        GrayRouteRulesCache.getInstance().addDubboGrayRulesDataListener();
    }
}
