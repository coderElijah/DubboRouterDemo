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

    @Override
    public String selectVersion() {
        return "i m " + dubboApplicationId;
    }
}
