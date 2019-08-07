package com.elijah.dubbograystarter.service;

import com.elijah.dubbograystarter.model.GrayRule;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.registry.support.ProviderInvokerWrapper;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.dubbo.rpc.cluster.Constants.DEFAULT_LOADBALANCE;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterconsumer
 * Date: 2019-08-03 12:51
 */
public class DubboGrayLoadBalance implements LoadBalance{
    public static final String NAME = "gray";
    private static final Logger logger = LoggerFactory.getLogger(DubboGrayLoadBalance.class);
    public DubboGrayLoadBalance(){
        logger.info("DubboGrayLoadBalance Init!");
    }
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        logger.info("use loadbalance {}", NAME);
        Map<String, Map<String, GrayRule>> grayRouteRulesMap = GrayRouteRulesCache.getInstance().getGrayRules();
        LoadBalance defualtLoadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(DEFAULT_LOADBALANCE);
        if (grayRouteRulesMap == null || grayRouteRulesMap.isEmpty()) {
            return defualtLoadBalance.select(invokers, url, invocation);
        }
        Object[] args = invocation.getArguments();
        if (args == null || !(args[0] instanceof String)) {
            return defualtLoadBalance.select(invokers, url, invocation);
        }
        String bizzKey = String.valueOf(args[0]);
        Iterator<Invoker<T>> invokerIterator = invokers.iterator();
        List<Invoker<T>> grayInvokerList = new ArrayList<>();
        while (invokerIterator.hasNext()){
            Invoker<T> invoker = invokerIterator.next();
            if(isGray(bizzKey,invoker,grayRouteRulesMap)){
                logger.info("join gray loadbalance:{}", bizzKey);
                grayInvokerList.add(invoker);
            }else {
                continue;
            }
        }
        // Done: 2019-08-07 不存在灰度路由则全部随机路由，若存在则灰度项随机路由
        if(grayInvokerList.isEmpty()){
            return defualtLoadBalance.select(invokers, url, invocation);
        }else{
            return defualtLoadBalance.select(grayInvokerList, url, invocation);
        }
    }


    /**
     * 判断是否参与灰度
     * @param bizzKey
     * @param invoker
     * @param grayRulesMap
     * @param <T>
     * @return
     */
    private <T> boolean isGray(String bizzKey,Invoker<T> invoker,Map<String,Map<String,GrayRule>> grayRulesMap) {
        URL providerUrl;
        if (invoker instanceof ProviderInvokerWrapper) {
            providerUrl = ((ProviderInvokerWrapper) invoker).getProviderUrl();
        }else{
            return false;
        }
        String applicationId = providerUrl.getParameter("application.id");
        if (applicationId == null || "".equals(applicationId)) {
            return false;
        }
        if (bizzKey == null || "".equals(bizzKey)) {
            return false;
        }

        if (!grayRulesMap.containsKey(bizzKey)) {
            return false;
        }
        Map<String, GrayRule> applicationMap = grayRulesMap.get(bizzKey);
        if (!applicationMap.containsKey(applicationId)) {
            return false;
        }
        GrayRule grayRule = applicationMap.get(applicationId);
        if (grayRule.getIsEnable() == 0) {
            return false;
        }
        return true;
    }
}
