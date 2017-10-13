package com.github.vaibhavsinha.jerrymouse;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.commons.cli.*;

/**
 * Created by vaibhav on 13/10/17.
 */
public class JerryMouse {

    public static void main(String[] args) throws ParseException, InterruptedException {
        JerryMouse jerryMouse = new JerryMouse();
        jerryMouse.run(getOptions(args));
    }

    private static CommandLine getOptions(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("host", true, "The IP address of the interface to listen for connections on. If empty, bind to localhost");
        options.addOption("port", true, "The port to start the server on. If empty, defaults to 8080");

        CommandLineParser parser = new DefaultParser();
        return parser.parse( options, args);
    }

    private void run(CommandLine cmd) throws InterruptedException {
        String host = cmd.getOptionValue("host", "localhost");
        Integer port = Integer.valueOf(cmd.getOptionValue("port", "8080"));

        EventLoopGroup eventLoopGroup = new OioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap().group(eventLoopGroup).localAddress(host, port).channel(OioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec()).addLast(new HttpObjectAggregator(512 * 1024)).addLast(new HttpRequestChannelHandler());
            }
        });
        bootstrap.bind().sync();
    }
}
