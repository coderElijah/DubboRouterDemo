package com.elijah.dubborouterconsumer;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Constants;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
public class AcmeGreyLoadBalance implements LoadBalance,Serializable {
    public static final String NAME = "grey";

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        Iterator<Invoker<T>> invokerIterator = invokers.iterator();
        while (invokerIterator.hasNext()){
            Invoker<T> invoker = invokerIterator.next();
            URL providerUrl = null;
            try {
                Field field = invoker.getClass().getDeclaredField("providerUrl");
                field.setAccessible(true);
                providerUrl = (URL) field.get(invoker);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            String version = providerUrl.getParameter("application");
            if (version != null && "dubbo-router-provider-v3.0.0".equals(version)) {
                return invoker;
            }
        }
        LoadBalance defualtLoadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(DEFAULT_LOADBALANCE);
        return defualtLoadBalance.select(invokers, url, invocation);
    }
}
