package com.elijah.dubbograystarter.service;

import com.elijah.dubbograystarter.model.GrayRule;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.registry.integration.RegistryProtocol;
import org.apache.dubbo.registry.support.ProviderInvokerWrapper;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.protocol.dubbo.DubboInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.elijah.dubbograystarter.Constant.*;
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
        Map<String, Map<String, String>> grayRouteRulesMap = GrayRouteRulesCache.getInstance().getGrayRules();
        LoadBalance defualtLoadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(DEFAULT_LOADBALANCE);
        if (grayRouteRulesMap == null || grayRouteRulesMap.isEmpty()) {
            return defualtLoadBalance.select(invokers, url, invocation);
        }
        Object[] args = invocation.getArguments();
        if (args == null || !(args[0] instanceof String)) {
            return defualtLoadBalance.select(invokers, url, invocation);
        }
        String bizzKey = String.valueOf(args[0]);
        if (bizzKey == null || "".equals(bizzKey)) {
            return defualtLoadBalance.select(invokers, url, invocation);
        }
        return defualtLoadBalance.select(this.getGrayInvoker(bizzKey, invokers, grayRouteRulesMap), url, invocation);
    }

    /**
     * 筛选路由invoker
     * @param bizzKey
     * @param invokers
     * @param grayRouteRulesMap
     * @param <T>
     * @return
     */
    private <T> List<Invoker<T>> getGrayInvoker(String bizzKey,List<Invoker<T>> invokers,Map<String, Map<String, String>> grayRouteRulesMap) {
        URL providerUrl;
        Iterator<Invoker<T>> iterator = invokers.iterator();
        List<Invoker<T>> newInvokers = new ArrayList<>();
        while(iterator.hasNext()){
            Invoker<T> invoker = iterator.next();
            try {
                Field field= invoker.getClass().getDeclaredField(Dubbo_Invoker_ProviderUrl_Field_Key);
                field.setAccessible(true);
                providerUrl = (URL) field.get(invoker);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Done: 2019-08-08 解析错误，则不进行此invoker的路由
                logger.info("获取providerUrl错误");
                newInvokers.add(invoker);
                continue;
            }
            String applicationId = providerUrl.getParameter(Dubbo_Provider_Gray_ApplicationId_Key);
            // Done: 2019-08-08 applicationid 为空则说明此invoker不参与灰度，全部可调用，则进行路由
            if (applicationId == null || "".equals(applicationId)) {
                newInvokers.add(invoker);
            }
            // Done: 2019-08-08 applicationtype为空则表示此服务应用不进行路由，全部可调用，则进行路由
            String applicationType = applicationId.substring(0, applicationId.indexOf("-"));
            if (!grayRouteRulesMap.containsKey(applicationType)) {
                newInvokers.add(invoker);
            }
            Map<String,String> bizzMap = grayRouteRulesMap.get(applicationType);
            // Done: 2019-08-08 存在此bizzid的路由规则 则判断此invoker是否是该bizzkey的对应应用
            if (bizzMap.containsKey(bizzKey)) {
                // Done: 2019-08-08 是对应应用则路由 否则不进行路由
                String invokeApplicationId = bizzMap.get(bizzKey);
                if (applicationId.equals(invokeApplicationId)) {
                    newInvokers.add(invoker);
                }else{
                    continue;
                }
            }else{
                // Done: 2019-08-08 不存在此bizzkey的路由规则,则此bizzkey走默认路由，判断此invoker是否是默认路由，是则添加invoker,否则不添加
                if(bizzMap.get(Default_Gray_Application_LoadBalance_Version).equals(applicationId)){
                    newInvokers.add(invoker);
                }else{
                    continue;
                }
            }
        }
        if (newInvokers == null || newInvokers.isEmpty()) {
            throw new RpcException("当前bizzKey无可用服务:bizzKey-" + bizzKey);
        }
        return newInvokers;
    }
    /**
     * 判断是否参与灰度
     * @param bizzKey
     * @param invoker
     * @param grayRulesMap
     * @param <T>
     * @return
     */
    @Deprecated
    private <T> boolean isGray(String bizzKey,Invoker<T> invoker,Map<String,Map<String,String>> grayRulesMap) {
        URL providerUrl;
        try {
            Field field= invoker.getClass().getDeclaredField(Dubbo_Invoker_ProviderUrl_Field_Key);
            field.setAccessible(true);
            providerUrl = (URL) field.get(invoker);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.info("获取providerUrl错误,过滤此路由灰度");
            return false;
        }
        String applicationId = providerUrl.getParameter(Dubbo_Provider_Gray_ApplicationId_Key);
        if (applicationId == null || "".equals(applicationId)) {
            return false;
        }
        String applicationType = applicationId.substring(0, applicationId.indexOf("-"));
        if (!grayRulesMap.containsKey(applicationType)) {
            return false;
        }
        Map<String,String> bizzMap = grayRulesMap.get(applicationType);
        if (!bizzMap.containsKey(bizzKey)) {
            return false;
        }
        return true;
    }
}
