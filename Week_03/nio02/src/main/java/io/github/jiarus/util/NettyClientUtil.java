package io.github.jiarus.util;

import io.github.jiarus.client.inbound.HttpInboundClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URISyntaxException;

/**
 * @author zhangjiaru
 * @date: 2021/01/29
 */
public class NettyClientUtil {
    
    public static void send(FullHttpRequest fullRequest, ChannelHandlerContext requestCtx, String url) throws InterruptedException, URISyntaxException {
        HttpInboundClient client = new HttpInboundClient();
        client.send(fullRequest, requestCtx, url);
    }
}
