package com.elijah.dubbograystarter.filter;

import com.elijah.dubbograystarter.model.GrayRule;
import com.elijah.dubbograystarter.service.GrayRouteRulesCache;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Map;

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
        Map<String, Map<String, GrayRule>> grayRulesMap = GrayRouteRulesCache.getInstance().getGrayRules();
        if (grayRulesMap.isEmpty() || !grayRulesMap.containsKey(bizzKey)) {
            return invoker.invoke(invocation);
        }
        Map<String, GrayRule> applicationMap = grayRulesMap.get(bizzKey);
        String applicationId = invoker.getUrl().getParameter("application.id");
        if (!applicationMap.containsKey(applicationId) || applicationMap.get(applicationId).getIsEnable() != 1) {
            throw new RpcException("灰度业务id不属于调用服务版本");
        }
        return invoker.invoke(invocation);
    }
}
