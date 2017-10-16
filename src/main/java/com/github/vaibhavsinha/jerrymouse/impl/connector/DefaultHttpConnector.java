package com.github.vaibhavsinha.jerrymouse.impl.connector;

import com.github.vaibhavsinha.jerrymouse.model.api.Connector;
import com.github.vaibhavsinha.jerrymouse.model.api.Container;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by vaibhav on 16/10/17.
 */
@Slf4j
public class DefaultHttpConnector implements Connector {

    private ServerBootstrap bootstrap;
    private ChannelFuture future;
    private boolean running = false;
    private Container container;

    @Override
    public void initialize() throws Exception {
        ServletRequestChannelHandler servletRequestChannelHandler = new ServletRequestChannelHandler(container);
        EventLoopGroup eventLoopGroup = new OioEventLoopGroup();
        bootstrap = new ServerBootstrap().group(eventLoopGroup).localAddress(ConfigUtils.config.getHost(), ConfigUtils.config.getPort()).channel(OioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(512 * 1024)).addLast(servletRequestChannelHandler);
            }
        });
    }

    @Override
    public void start() throws Exception {
        future = bootstrap.bind().sync();
        log.info("Server started on port " + ConfigUtils.config.getPort());
        running = true;
    }

    @Override
    public void stop() throws Exception {
        running = false;
        future.channel().close().sync();
    }

    @Override
    public boolean isAvailable() {
        return running;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

}
