package com.elijah.dubborouterprovider.provider;

import com.elijah.dubboroutercommon.DubboRouterService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterprovider
 * Date: 2019-08-02 23:12
 */
@Service
@Component
public class IDubboRouterServiceV1 implements DubboRouterService {
    @Value("${dubbo.application.id}")
    String dubboApplicationId;
    @Value("${dubbo.registry.address}")
    String zookeeperAddress;

    @Override
    public String selectVersion() {
        return "i m " + dubboApplicationId;
    }

//    @PostConstruct
//    public void refisterRouteRules(){
//        List<GrayRule> grayRules = new ArrayList<>();
//        GrayRule grayRule = new GrayRule();
//        grayRule.setBizzKey("123");
//        grayRule.setApplicationId("dubbo-router-provider-v1.0.0");
//        grayRule.setIsEnable(1);
//        grayRule.setCreateTime(System.currentTimeMillis());
//        grayRules.add(grayRule);
//        GrayRule grayRule1 = new GrayRule();
//        grayRule1.setBizzKey("456");
//        grayRule1.setApplicationId("dubbo-router-provider-v2.0.0");
//        grayRule1.setIsEnable(1);
//        grayRule1.setCreateTime(System.currentTimeMillis());
//        grayRules.add(grayRule1);
////        GrayRouteRulesCache.getInstance().addZkRouteRulesCach(zookeeperAddress,"/CustomRouterRules/Acme/routerrules", grayRules);
//    }
}
