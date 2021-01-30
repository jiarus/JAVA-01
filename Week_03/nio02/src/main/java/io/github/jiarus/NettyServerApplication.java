package io.github.jiarus;

import io.github.jiarus.server.inbound.HttpInboundServer;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhangjiaru
 * @date: 2021/01/25
 */
public class NettyServerApplication {
    
    public static void main(String[] args) {
        int proxyPort = Integer.parseInt(System.getProperty("proxyPort", "8888"));
        //proxyServers
        String proxyServers = System.getProperty("proxyServers", "http://localhost:8088");
        List<String> proxyHosts = Arrays.asList(proxyServers.split(","));
        //多线程 bio网关
//        HttpServer server = new HttpMulThreadServer(proxyPort, Arrays.asList(proxyServers.split(",")));
        //nio网关
        HttpInboundServer server = new HttpInboundServer(proxyPort, proxyHosts);
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
