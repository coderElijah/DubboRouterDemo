package com.elijah.dubborouterprovider.provider;

import com.elijah.dubboroutercommon.DubboRouterService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.remoting.zookeeper.DataListener;
import org.apache.dubbo.remoting.zookeeper.EventType;
import org.apache.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void refisterRouteRules(){
        ZookeeperTransporter curator = ExtensionLoader.getExtensionLoader(ZookeeperTransporter.class).getExtension("curator");
        URL zookeeperUrl = new URL("zookeeper", "127.0.0.1", 2181);
        ZookeeperClient zkClient = curator.connect(zookeeperUrl);
        zkClient.create("/CustomRouterRules/Acme/routerrules", "user1=" + dubboApplicationId, Boolean.TRUE);
    }
}
