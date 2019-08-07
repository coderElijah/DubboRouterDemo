package com.elijah.dubbograystarter.controller;

import com.elijah.dubbograystarter.service.GrayRouteRulesCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubbograystarter.controller
 * Date: 2019-08-07 13:54
 */
@RestController
public class DubboGrayController {
    @Autowired
    GrayRouteRulesCache grayRouteRulesCache;

    @GetMapping("dubboGray/getGrayRules")
    public Map getGrayRules(){
        return grayRouteRulesCache.getGrayRules();
    }
}
