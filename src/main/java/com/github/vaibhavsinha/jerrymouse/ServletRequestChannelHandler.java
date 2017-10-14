package com.github.vaibhavsinha.jerrymouse;

import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletRequest;
import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletResponse;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletMappingType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletType;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vaibhav on 13/10/17.
 */
public class ServletRequestChannelHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Servlet> servletMap = new HashMap<>();

    public ServletRequestChannelHandler() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        for(Object obj: ConfigUtils.webApp.getObjects()) {
            if(obj instanceof ServletType && ((ServletType) obj).getLoadOnStartup().getValue().equals(BigInteger.ONE)) {
                ServletType servletObj = (ServletType) obj;
                loadServlet(servletObj);
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;

        JerryMouseServletRequest servletRequest = new JerryMouseServletRequest(request);
        JerryMouseServletResponse servletResponse = new JerryMouseServletResponse();

        String url = request.uri().split("\\?")[0];
        if(isValidUrl(url)) {
            Servlet servlet = servletMap.get(url);
            if(servlet == null) {
                loadServlet(getServletTypeByName(getServletNameByUrl(url)));
                servlet = servletMap.get(url);
            }

            servlet.service(servletRequest, servletResponse);
            servletResponse.addIntHeader("Content-Length", servletResponse.getFullHttpResponse().content().readableBytes());
        }
        else {
            servletResponse.setStatus(404);
            servletResponse.addIntHeader("Content-Length", 0);
        }
        ctx.writeAndFlush(servletResponse.getFullHttpResponse());
    }

    @SuppressWarnings("unchecked")
    private void loadServlet(ServletType servletObj) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<Servlet> servletClass = (Class<Servlet>) ConfigUtils.loader.loadClass((servletObj).getServletClass().getValue());
        List<String> urlsForServlet = getUrlsForServlet(servletObj.getServletName().getValue());
        Servlet instance = servletClass.newInstance();
        urlsForServlet.forEach(url -> servletMap.put(url, instance));
    }

    private Boolean isValidUrl(String url) {
        for(Object obj: ConfigUtils.webApp.getObjects()) {
            if(obj instanceof ServletMappingType && ((ServletMappingType) obj).getUrlPattern().getValue().equals(url)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getUrlsForServlet(String name) {
        return ConfigUtils.webApp.getObjects().stream().filter(obj -> obj instanceof ServletMappingType && ((ServletMappingType) obj).getServletName().getValue().equals(name)).map(obj -> ((ServletMappingType) obj).getUrlPattern().getValue()).collect(Collectors.toList());
    }

    private ServletType getServletTypeByName(String name) {
        return (ServletType) ConfigUtils.webApp.getObjects().stream().filter(obj -> obj instanceof ServletType && ((ServletType) obj).getServletName().getValue().equals(name)).findFirst().get();
    }

    private String getServletNameByUrl(String url) {
        return ((ServletMappingType) ConfigUtils.webApp.getObjects().stream().filter(obj -> obj instanceof ServletMappingType && ((ServletMappingType) obj).getUrlPattern().getValue().equals(url)).findFirst().get()).getServletName().getValue();
    }
}
