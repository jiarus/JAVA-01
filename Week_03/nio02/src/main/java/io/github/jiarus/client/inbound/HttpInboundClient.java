package io.github.jiarus.client.inbound;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author zhangjiaru
 * @date: 2021/01/29
 */
public class HttpInboundClient {
    
    public void send(FullHttpRequest fullRequest, ChannelHandlerContext requestCtx, String url) throws InterruptedException, URISyntaxException {
        URI uri = new URI(url);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //客户端
            Bootstrap bootstrap = new Bootstrap();
            //绑定bossGroup
            bootstrap.group(group)
                    //指定channel类型，异步客户端Socket Channel
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new HttpResponseDecoder());
                            // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                            ch.pipeline().addLast(new HttpRequestEncoder());
                            ch.pipeline().addLast(new HttpInboundHandler(fullRequest, requestCtx));
                        }
                    });
            
            ChannelFuture cf = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(),
                    Unpooled.wrappedBuffer("".getBytes()));
            // 构建http请求
            request.headers().set(HttpHeaderNames.HOST, uri.getHost());
            request.headers().set(HttpHeaderNames.CONNECTION,
                    HttpHeaderNames.CONNECTION);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                    request.content().readableBytes());
            cf.channel().writeAndFlush(request);
            cf.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
