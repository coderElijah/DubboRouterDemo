package com.elijah.dubbograystarter.filter;

import com.elijah.dubbograystarter.model.GrayRule;
import com.elijah.dubbograystarter.service.GrayRouteRulesCache;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.elijah.dubbograystarter.Constant.Dubbo_Provider_Gray_ApplicationId_Key;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:DubboRouterDemo
 * PackageName: com.elijah.dubbograystarter.filter
 * Date: 2019-08-07 16:56
 */
@Activate(group = "provider")
public class DubboGrayProviderFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DubboGrayProviderFilter(){
        logger.info("DubboGrayProviderFilter Init!");
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Object[] args = invocation.getArguments();
        if (args == null || args.length == 0) {
            return invoker.invoke(invocation);
        }
        if (!(args[0] instanceof String)) {
            return invoker.invoke(invocation);
        }
        if (args[0] == null || "".equals(args[0])) {
            return invoker.invoke(invocation);
        }
        String bizzKey = String.valueOf(args[0]);
        String invokeApplicationId = invocation.getInvoker().getUrl().getParameter(Dubbo_Provider_Gray_ApplicationId_Key);
        if (invokeApplicationId == null || "".equals(invokeApplicationId)) {
            return invoker.invoke(invocation);
        }
        String invokeApplicationName = invokeApplicationId.substring(0, invokeApplicationId.indexOf("-"));
        if (invokeApplicationName == null || "".equals(invokeApplicationName)) {
            return invoker.invoke(invocation);
        }
        Map<String, Map<String, String>> grayRulesMap = GrayRouteRulesCache.getInstance().getGrayRules();
        if (grayRulesMap.isEmpty() || !grayRulesMap.containsKey(invokeApplicationName)) {
            return invoker.invoke(invocation);
        }
        Map<String, String> bizzMap = grayRulesMap.get(invokeApplicationName);
        ApplicationConfig applicationConfig = ConfigManager.getInstance().getApplication().get();
        if (bizzMap.containsKey(bizzKey)) {
            if (!invokeApplicationId.equals(bizzMap.get(bizzKey))) {
                throw new RpcException("灰度业务id不属于调用服务版本" +
                        "\tbizzKey:" + bizzKey +
                        "\tapplicatioName:" + applicationConfig.getName());
            }else{
                return invoker.invoke(invocation);
            }
        }else{
            if (invokeApplicationId.equals(applicationConfig.getName())) {
                return invoker.invoke(invocation);
            }else{
                throw new RpcException("灰度业务id不属于调用服务版本" +
                        "\tbizzKey:" + bizzKey +
                        "\tapplicatioName:" + applicationConfig.getName());
            }
        }
    }
}
