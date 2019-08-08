package com.elijah.dubbograystarter.config;

import com.elijah.dubbograystarter.controller.DubboGrayController;
import com.elijah.dubbograystarter.model.GrayRule;
import com.elijah.dubbograystarter.service.GrayRouteRulesCache;
import com.elijah.dubbograystarter.service.GrayRouterApplicationStartListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubbograystarter
 * Date: 2019-08-06 15:23
 */
@Configuration
@ConditionalOnClass(GrayRouteRulesCache.class)
@EnableConfigurationProperties(DubboGrayProperties.class)
public class DubboGrayAutoConfiguration {
    private Logger logger = LoggerFactory.getLogger(DubboGrayAutoConfiguration.class);
    @Autowired
    DubboGrayProperties dubboGrayProperties;

    @Bean
    @ConditionalOnMissingBean(GrayRouteRulesCache.class)
    public GrayRouteRulesCache grayRouteRulesCache(){
        logger.info("设置灰度路由监控节点路径:{}",dubboGrayProperties.getGrayRulesNodePath());
        GrayRouteRulesCache.setZkGrayRulesNodePath(dubboGrayProperties.getGrayRulesNodePath());
        logger.info("设置灰度路由监控zk路径:{}",dubboGrayProperties.getAddress());
        GrayRouteRulesCache.setRegistUrl(dubboGrayProperties.getAddress());
        return GrayRouteRulesCache.getInstance();
    }
    @Bean
    @ConditionalOnMissingBean(GrayRouterApplicationStartListener.class)
    public GrayRouterApplicationStartListener grayRouterApplicationStartListener(){
        return new GrayRouterApplicationStartListener();
    }
    @Bean
    @ConditionalOnMissingBean
    public DubboGrayController dubboGrayController(){
        return new DubboGrayController();
    }
}
