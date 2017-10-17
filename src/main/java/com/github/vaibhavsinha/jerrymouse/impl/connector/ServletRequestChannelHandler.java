package com.github.vaibhavsinha.jerrymouse.impl.connector;

import com.github.vaibhavsinha.jerrymouse.model.api.Container;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by vaibhav on 13/10/17.
 */
@Slf4j
@ChannelHandler.Sharable
public class ServletRequestChannelHandler extends ChannelInboundHandlerAdapter {

    private Container container;

    public ServletRequestChannelHandler(Container container) {
        this.container = container;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest request = (FullHttpRequest) msg;
        DefaultConnectorServletRequest servletRequest = new DefaultConnectorServletRequest(request);
        DefaultConnectorServletResponse servletResponse = new DefaultConnectorServletResponse();

        container.invoke(servletRequest, servletResponse);
        ctx.writeAndFlush(servletResponse.getFullHttpResponse());
        ((FullHttpRequest) msg).release();
    }
}
