package com.elijah.dubbograystarter.config;

import com.elijah.dubbograystarter.model.GrayRulesType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubbograystarter
 * Date: 2019-08-06 15:20
 */
@ConfigurationProperties(prefix = "dubbo.registry")
public class DubboGrayProperties {
    private String address = "zookeeper://127.0.0.1:2181";
    private String grayRulesNodePath = "/customrouterrules/routerrules";

    public String getGrayRulesNodePath() {
        return grayRulesNodePath;
    }

    public void setGrayRulesNodePath(String grayRulesNodePath) {
        this.grayRulesNodePath = grayRulesNodePath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
