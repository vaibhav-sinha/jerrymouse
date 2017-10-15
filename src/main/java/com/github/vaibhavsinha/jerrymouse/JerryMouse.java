package com.github.vaibhavsinha.jerrymouse;

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
import org.apache.commons.cli.ParseException;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by vaibhav on 13/10/17.
 */
@Slf4j
public class JerryMouse {

    private ChannelFuture future;

    public static void main(String[] args) throws ParseException, InterruptedException, IOException, ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        JerryMouse jerryMouse = new JerryMouse();
        jerryMouse.setupShutdownHook();
        jerryMouse.run();
    }

    private void run() throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, ServletException, IOException {
        ServletRequestChannelHandler servletRequestChannelHandler = new ServletRequestChannelHandler();
        EventLoopGroup eventLoopGroup = new OioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap().group(eventLoopGroup).localAddress(ConfigUtils.config.getHost(), ConfigUtils.config.getPort()).channel(OioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(512 * 1024)).addLast(servletRequestChannelHandler);
            }
        });
        future = bootstrap.bind().sync();
        log.info("Server started on port " + ConfigUtils.config.getPort());
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(future != null && future.channel().isActive()) {
                future.channel().close();
            }
        }));
    }
}
