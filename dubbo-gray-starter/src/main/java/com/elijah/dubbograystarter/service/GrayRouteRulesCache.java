package com.elijah.dubbograystarter.service;

import com.elijah.dubbograystarter.model.GrayRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.apache.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newFixedThreadPool;

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
    private static class GrayRouteRulesCacheHolder {
        static final GrayRouteRulesCache INSTANCE = new GrayRouteRulesCache();
    }

    private static volatile String registUrl;
    private static volatile String zkGrayRulesNodePath;
    private static final Logger logger = LoggerFactory.getLogger(GrayRouteRulesCache.class);
    private static volatile ConcurrentHashMap<String, Map<String, GrayRule>> grayRouteRulesMap = new ConcurrentHashMap<>(16);
    private Executor executor = newFixedThreadPool(3, new NamedThreadFactory(this.getClass().getSimpleName(), true));

    private GrayRouteRulesCache() {
    }

    public static GrayRouteRulesCache getInstance() {
        return GrayRouteRulesCacheHolder.INSTANCE;
    }

    public static void setRegistUrl(String customRegistUrl) {
        registUrl = customRegistUrl;
    }
    public static void setZkGrayRulesNodePath(String customNodePath){
        zkGrayRulesNodePath = customNodePath;
    }


    /**
     * 刷新本地规则缓存
     * @return
     */
    public boolean refreshLocalRules() {
        ZookeeperClient zookeeperClient = getZookeeperClient();
        String rules = zookeeperClient.getContent(zkGrayRulesNodePath);
        Map<String, Map<String, GrayRule>> rulesMap = null;
        if (rules != null && !"".equals(rules)) {
            try {
                rulesMap = new ObjectMapper().readValue(rules, ConcurrentHashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("刷新路由规则json解析错误，规则需为json串");
            }
        }
        if (rulesMap == null || rulesMap.isEmpty()) {
            logger.info("无可刷新路由规则");
            return false;
        }
        grayRouteRulesMap = (ConcurrentHashMap<String, Map<String, GrayRule>>) rulesMap;
        logger.info("路由规则已刷新");
        return true;
    }

    /**
     * 向远程zk添加路由规则
     * @param grayRules
     * @return
     */
    public boolean addZkRouteRulesCach(List<GrayRule> grayRules) {
        if (grayRules == null || grayRules.isEmpty()) {
            return false;
        }
        refreshLocalRules();
        Map<String, Map<String, GrayRule>> grayRuleMap = this.getGrayRules();
        grayRules.forEach(grayRule -> {
            if (grayRuleMap.containsKey(grayRule.getBizzKey())) {
                grayRuleMap.get(grayRule.getBizzKey()).put(grayRule.getApplicationId(), grayRule);
            } else {
                Map<String, GrayRule> applicationMap = new HashMap<>(16);
                applicationMap.put(grayRule.getApplicationId(), grayRule);
                grayRuleMap.put(grayRule.getBizzKey(), applicationMap);
            }
        });
        ZookeeperClient zkClient = getZookeeperClient();
        String context;
        try {
            context = new ObjectMapper().writeValueAsString(grayRuleMap);
        } catch (JsonProcessingException e) {
            logger.error("路由规则json序列化错误");
            e.printStackTrace();
            return false;
        }
        //创建持久节点
        zkClient.create(zkGrayRulesNodePath, context, Boolean.FALSE);
        return true;
    }

    private ZookeeperClient getZookeeperClient() {
        // TODO: 2019-08-06 路径解析待优化
        String protocalName = registUrl.substring(0, registUrl.indexOf("://"));
        String address = registUrl.substring(registUrl.indexOf("://") + 3, registUrl.lastIndexOf(":"));
        Integer port = Integer.parseInt(registUrl.substring(registUrl.lastIndexOf(":") + 1));
        ZookeeperTransporter curator = ExtensionLoader.getExtensionLoader(ZookeeperTransporter.class).getExtension("curator");
        URL zookeeperUrl = new URL(protocalName, address, port);
        return curator.connect(zookeeperUrl);
    }

    /**
     * 获取本地缓存中的规则
     *
     * @return
     */
    public Map<String, Map<String, GrayRule>> getGrayRules() {
        return grayRouteRulesMap;
    }

    /**
     * 添加规则zk节点监听
     */
    public void addDubboGrayRulesDataListener() {
        ZookeeperClient zookeeperClient = getZookeeperClient();
        zookeeperClient.addDataListener(zkGrayRulesNodePath, (path, value, eventType) -> {
            switch (eventType) {
                case NodeDataChanged:
                case NodeCreated:
                case INITIALIZED:
                case CONNECTION_RECONNECTED:
                    refreshLocalRules();
                    break;
                default:
                    getZookeeperClient();
                    break;
            }
        }, executor);
    }

}
