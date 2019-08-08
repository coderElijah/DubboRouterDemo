package com.elijah.dubbograystarter.controller;

import com.elijah.dubbograystarter.model.GrayRule;
import com.elijah.dubbograystarter.service.GrayRouteRulesCache;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public DubboGrayController(){
        logger.info("DubboGrayController Init!");
    }
    @Autowired
    GrayRouteRulesCache grayRouteRulesCache;

    @GetMapping("dubboGray/getGrayRules")
    public Map getGrayRules() {
        return grayRouteRulesCache.getGrayRules();
    }

    @PostMapping("dubboGray/addTestGrayRules")
    public boolean addTestGrayRules(String rules) throws IOException {
        GrayRouteRulesCache.getInstance().addZkRouteRulesCach(new ObjectMapper().readValue(rules,new TypeReference<List<GrayRule>>(){}));
        return Boolean.TRUE;
    }
}
