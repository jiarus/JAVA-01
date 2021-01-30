package io.github.jiarus.client.inbound;

import io.github.jiarus.util.ByteBufToBytes;
import io.github.jiarus.util.NettyResponseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

/**
 * @author zhangjiaru
 * @date: 2021/01/29
 */
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    private ByteBufToBytes reader;
    private FullHttpRequest fullRequest;
    private ChannelHandlerContext requestCtx;
    
    public HttpInboundHandler(FullHttpRequest fullRequest, ChannelHandlerContext requestCtx) {
        this.fullRequest = fullRequest;
        this.requestCtx = requestCtx;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext proxyCtx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.out.println("CONTENT_TYPE:"
                    + response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            if (HttpUtil.isContentLengthSet(response)) {
                reader = new ByteBufToBytes(
                        (int) HttpUtil.getContentLength(response));
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            reader.reading(content);
            content.release();
            if (reader.isEnd()) {
                String resultStr = new String(reader.readFull());
                System.out.println("Server said:" + resultStr);
                proxyCtx.close();
                NettyResponseUtil.handleNettyResponse(fullRequest, requestCtx, resultStr);
            }
        }
    }
    
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("发生异常");
        ctx.close();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("读取消息结束");
    }
}
