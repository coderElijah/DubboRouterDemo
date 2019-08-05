package com.elijah.dubborouterprovider.provider;

import com.elijah.dubborouterprovider.GrayRule;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.utilities.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterprovider.provider
 * Date: 2019-08-05 15:36
 */
public class GrayRouteRulesCache {
    private static class GrayRouteRulesCacheHolder{
        static final GrayRouteRulesCache INSTANCE = new GrayRouteRulesCache();
    }

    private static final Logger logger = LoggerFactory.getLogger(GrayRouteRulesCache.class);
    private static final ConcurrentHashMap<String, GrayRule> grayRouteRulesMap = new ConcurrentHashMap<>(16);

    private GrayRouteRulesCache(){
    }

    public GrayRouteRulesCache getInstance(){
        return GrayRouteRulesCacheHolder.INSTANCE;
    }
    public boolean refreshLocalRules(String registerUrl,String rulePath){
        ZookeeperTransporter zookeeperTransporter = ExtensionLoader.getExtensionLoader(ZookeeperTransporter.class).getExtension("curator");
        String port = registerUrl.substring(registerUrl.indexOf(':') + 1);
        Assert.that(port == null || "".equals(port), "路由存储zookeeper端口不可为空");
        URL zkUrl = null;
        if(port!=null&&!"".equals(port)){
            zkUrl = new URL("zookeeper", registerUrl, Integer.parseInt(port));
        }
        ZookeeperClient zookeeperClient = zookeeperTransporter.connect(zkUrl);
        String rules = zookeeperClient.getContent(rulePath);
        synchronized (grayRouteRulesMap){
            String[] ruleArr = rules.split(";");
            for (String s : ruleArr) {
                int index = s.indexOf("=");
                Assert.that(index == -1,"路由规则配置错误");
                String bizzKey = s.substring(0, index);
                String applicationId = s.substring(index + 1, s.length() - 1);
                grayRouteRulesMap.put(bizzKey, new GrayRule(bizzKey, applicationId));
            }
        }
        return true;
    }

}
