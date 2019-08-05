package com.elijah.dubborouterconsumer;

import com.alibaba.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.zookeeper.ZookeeperClient;
import org.apache.dubbo.remoting.zookeeper.ZookeeperTransporter;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.protocol.InvokerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubborouterconsumer
 * Date: 2019-08-03 10:48
 */
//@Activate(group = Constants.CONSUMER,order = -9999999)
public class ConsumerFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String version = "v2.0.0";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ZookeeperTransporter curtor = ExtensionLoader.getExtensionLoader(ZookeeperTransporter.class).getExtension("curator");
        ZookeeperClient zkClient = curtor.connect(invoker.getUrl());
        zkClient.create("/CustomRouteRules/AcmeRouteRules/elijahtest1=dubbo-router-provider-v1.0.0&elijahtest2=dubbo-router-provider-v2.0.0",Boolean.TRUE);

        logger.info("----------------i am filter");
        URL url = invoker.getUrl();
        String version = url.getParameter("dubbo.tag");
        return invoker.invoke(invocation);
    }
}
