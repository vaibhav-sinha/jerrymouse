package com.github.vaibhavsinha.jerrymouse;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

/**
 * Created by vaibhav on 13/10/17.
 */
public class HttpRequestChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        HttpResponse httpResponse;
        URL resourceUrl = getClass().getResource(request.uri());
        if(resourceUrl != null) {
            File file = new File(resourceUrl.getFile());
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(Files.readAllBytes(file.toPath())));
            httpResponse.headers().add("Content-Length", file.length());
        }
        else {
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            httpResponse.headers().add("Content-Length", 0);
        }
        ctx.writeAndFlush(httpResponse);
    }
}
