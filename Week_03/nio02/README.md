#Netty Gateway 思路
>网关基本逻辑:  
    request -> HttpServer -> filter -> router - > backend-server

为什么使用Netty改造网关？
> 1.HttpServer端如果是使用单线程/多线程，同步阻塞IO模型，请求与内部处理线程是一对一的关系，并发量高会消耗大量资源。
> 2.Netty基于Reactor模型,使用select处理请求，可以实现异步的单线程/多线程/主从模式，消耗少量线程可以处理大量并发。

第5节课作业实践
1、按今天的课程要求，实现一个网关，基础代码可以 fork:https://github.com/kimmking/JavaCourseCodes 02nio/nio02 文件夹下
实现以后，代码提交到 Github。 

1)周三作业:(必做)整合你上次作业的httpclient/okhttp; 

[HttpInboundServer](src/main/java/io/github/jiarus/server/inbound/HttpInboundServer.java)

2)周三作业(可选):使用netty实现后端http访问(代替上一步骤); 

[HttpInboundClient](src/main/java/io/github/jiarus/client/inbound/HttpInboundHandler.java)

3)周日作业:(必做)实现过滤器 ~
[HeaderHttpRequestFilter](src/main/java/io/github/jiarus/server/filter/HeaderHttpRequestFilter.java)

4)周日作业(可选):实现路由

[HttpInboundClient](src/main/java/io/github/jiarus/server/router/RandomHttpEndpointRouter.java)
