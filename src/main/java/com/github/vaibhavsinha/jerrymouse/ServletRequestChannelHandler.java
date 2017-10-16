package com.github.vaibhavsinha.jerrymouse;

import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletConfig;
import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletRequest;
import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletResponse;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.*;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.lang.String;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
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
        for(JAXBElement obj: ConfigUtils.webApp.getObjects()) {
            if(obj.getDeclaredType() == ParamValueType.class) {
                applicationContext.addParamValueType((ParamValueType)obj.getValue());
            }
            if(obj.getDeclaredType() == ServletType.class && ((ServletType) obj.getValue()).getLoadOnStartup().equals(BigInteger.ONE.toString())) {
                ServletType servletObj = (ServletType) obj.getValue();
                loadServlet(servletObj);
            }
            if(obj.getDeclaredType() == ListenerType.class) {
                Class<EventListener> listenerClass = (Class<EventListener>) ConfigUtils.loader.loadClass(((ListenerType) obj.getValue()).getListenerClass().getValue());
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
        for(JAXBElement obj: ConfigUtils.webApp.getObjects()) {
            if(obj.getDeclaredType() == ServletMappingType.class) {
                for(UrlPatternType patternType : ((ServletMappingType) obj.getValue()).getUrlPattern()) {
                    if(url.startsWith(patternType.getValue())) {
                        return url.replace(patternType.getValue(), "");
                    }
                }
            }
        }
        return null;
    }

    private List<String> getUrlsForServlet(String name) {
        return ConfigUtils.webApp.getObjects().stream().filter(obj -> obj.getDeclaredType() == ServletMappingType.class && ((ServletMappingType) obj.getValue()).getServletName().getValue().equals(name)).flatMap(obj -> ((ServletMappingType) obj.getValue()).getUrlPattern().stream()).map(UrlPatternType::getValue).collect(Collectors.toList());
    }

    private ServletType getServletTypeByName(String name) {
        return (ServletType) ConfigUtils.webApp.getObjects().stream().filter(obj -> obj.getDeclaredType() == ServletType.class && ((ServletType) obj.getValue()).getServletName().getValue().equals(name)).findFirst().get().getValue();
    }

    private String getServletNameByUrl(String url) {
        return ((ServletMappingType) ConfigUtils.webApp.getObjects().stream().filter(obj -> obj.getDeclaredType() == ServletMappingType.class).filter(smt -> {
            for(UrlPatternType upt : ((ServletMappingType) smt.getValue()).getUrlPattern()) {
                if(url.startsWith(upt.getValue())) {
                    return true;
                }
            }
            return false;
        }).findFirst().get().getValue()).getServletName().getValue();
    }

}
