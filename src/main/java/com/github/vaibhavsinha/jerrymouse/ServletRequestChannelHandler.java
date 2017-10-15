package com.github.vaibhavsinha.jerrymouse;

import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletConfig;
import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletRequest;
import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletResponse;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ListenerType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ParamValueType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletMappingType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletType;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vaibhav on 13/10/17.
 */
@Slf4j
@ChannelHandler.Sharable
public class ServletRequestChannelHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Servlet> servletMap = new HashMap<>();
    private ApplicationContext applicationContext;
    private List<EventListener> eventListeners = new ArrayList<>();

    public ServletRequestChannelHandler() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, ServletException {
        Thread.currentThread().setContextClassLoader(ConfigUtils.loader);
        applicationContext = new ApplicationContext();
        for(Object obj: ConfigUtils.webApp.getObjects()) {
            if(obj instanceof ParamValueType) {
                applicationContext.addParamValueType((ParamValueType)obj);
            }
            if(obj instanceof ServletType && ((ServletType) obj).getLoadOnStartup().getValue().toString().equals(BigInteger.ONE.toString())) {
                ServletType servletObj = (ServletType) obj;
                loadServlet(servletObj);
            }
            if(obj instanceof ListenerType) {
                Class<EventListener> listenerClass = (Class<EventListener>) ConfigUtils.loader.loadClass(((ListenerType) obj).getListenerClass().getValue().toString());
                eventListeners.add(listenerClass.newInstance());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;

        JerryMouseServletRequest servletRequest = new JerryMouseServletRequest(request);
        JerryMouseServletResponse servletResponse = new JerryMouseServletResponse();

        String url = request.uri().split("\\?")[0];
        String pathInfo = isValidUrl(url);

        if(pathInfo != null) {
            servletRequest.setPathInfo(pathInfo);
            String matchedUrl = url.replace(pathInfo, "");
            Servlet servlet = servletMap.get(matchedUrl);
            if(servlet == null) {
                String servletName = getServletNameByUrl(matchedUrl);
                ServletType servletType = getServletTypeByName(servletName);
                loadServlet(servletType);
                servlet = servletMap.get(matchedUrl);
            }
            servletRequest.setServletPath(matchedUrl);

            servlet.service(servletRequest, servletResponse);
            servletResponse.addIntHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), servletResponse.getFullHttpResponse().content().readableBytes());
        }
        else {
            servletResponse.setStatus(404);
            servletResponse.addIntHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), 0);
        }
        ctx.writeAndFlush(servletResponse.getFullHttpResponse());
        ((FullHttpRequest) msg).release();
    }

    @SuppressWarnings("unchecked")
    private void loadServlet(ServletType servletObj) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ServletException {
        Class<Servlet> servletClass = (Class<Servlet>) ConfigUtils.loader.loadClass((servletObj).getServletClass().getValue());
        List<String> urlsForServlet = getUrlsForServlet(servletObj.getServletName().getValue());
        Servlet instance = servletClass.newInstance();
        JerryMouseServletConfig config = new JerryMouseServletConfig();
        config.setName(servletObj.getServletName().getValue());
        config.setParamValueTypeList(servletObj.getInitParam());
        config.setApplicationContext(applicationContext);
        instance.init(config);
        urlsForServlet.forEach(url -> servletMap.put(url, instance));
    }

    private String isValidUrl(String url) {
        for(Object obj: ConfigUtils.webApp.getObjects()) {
            if(obj instanceof ServletMappingType && url.startsWith(((ServletMappingType) obj).getUrlPattern().getValue())) {
                return url.replace(((ServletMappingType) obj).getUrlPattern().getValue(), "");
            }
        }
        return null;
    }

    private List<String> getUrlsForServlet(String name) {
        return ConfigUtils.webApp.getObjects().stream().filter(obj -> obj instanceof ServletMappingType && ((ServletMappingType) obj).getServletName().getValue().equals(name)).map(obj -> ((ServletMappingType) obj).getUrlPattern().getValue()).collect(Collectors.toList());
    }

    private ServletType getServletTypeByName(String name) {
        return (ServletType) ConfigUtils.webApp.getObjects().stream().filter(obj -> obj instanceof ServletType && ((ServletType) obj).getServletName().getValue().equals(name)).findFirst().get();
    }

    private String getServletNameByUrl(String url) {
        return ((ServletMappingType) ConfigUtils.webApp.getObjects().stream().filter(obj -> obj instanceof ServletMappingType && url.startsWith(((ServletMappingType) obj).getUrlPattern().getValue())).findFirst().get()).getServletName().getValue();
    }
}
