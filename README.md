# DubboRouterDemo
dubbo重写loadbalance实现灰度发布demo

### 项目背景
  由于项目中使用的是dubbo，2B多租户系统的业务中，客户需要分多版本的需求，以及dubbo服务部署需要灰度的小白鼠，在此背景下故需要dubbo服务的灰度发布机制，而dubbo admin管理台中，并不支持根据参数路由，故需要自己实现业务id的路由转发逻辑
### 原理
  在dubbo的调用链中，cluster挑选invoker进行调用时，会调用loadbanlance接口的select方法，进行负载策略，故使用dubbo的spi机制，可以自定义loadbanlance方法来加入灰度发布的逻辑。
  灰度发布还有一个问题就是路由规则的同步，由于项目中使用的是zk注册中心，则可以将路由规则放入zk节点中，使用zk的回调机制，来更新各dubbo服务的缓存，刷新路由规则

