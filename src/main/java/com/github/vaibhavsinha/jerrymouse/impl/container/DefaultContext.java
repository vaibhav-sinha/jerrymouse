package com.github.vaibhavsinha.jerrymouse.impl.container;

import com.github.vaibhavsinha.jerrymouse.ApplicationContext;
import com.github.vaibhavsinha.jerrymouse.impl.connector.DefaultConnectorServletResponse;
import com.github.vaibhavsinha.jerrymouse.model.api.Container;
import com.github.vaibhavsinha.jerrymouse.model.api.Context;
import com.github.vaibhavsinha.jerrymouse.model.api.Mapper;
import com.github.vaibhavsinha.jerrymouse.model.api.Wrapper;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.*;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import io.netty.handler.codec.http.HttpHeaderNames;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * Created by vaibhav on 16/10/17.
 */
public class DefaultContext extends DefaultAbstractContainer implements Context {

    private WebAppType webAppObj;
    private Mapper mapper;
    private ApplicationContext applicationContext;
    private List<EventListener> eventListeners = new ArrayList<>();


    @Override
    public void invoke(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        DefaultConnectorServletResponse servletResponse = (DefaultConnectorServletResponse) response;
        Container wrapper = mapper.map(request);
        if(wrapper != null) {
            wrapper.invoke(request, response);
            servletResponse.addIntHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), servletResponse.getFullHttpResponse().content().readableBytes());
        }
        else {
            servletResponse.setStatus(404);
            servletResponse.addIntHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), 0);
        }
    }

    @Override
    public void init() throws Exception {
        mapper = new DefaultMapper();
        mapper.setContainer(this);

        applicationContext = new ApplicationContext();
        for(JAXBElement obj: webAppObj.getObjects()) {
            if(obj.getDeclaredType() == ParamValueType.class) {
                applicationContext.addParamValueType((ParamValueType)obj.getValue());
            }
            if(obj.getDeclaredType() == ServletType.class) {
                Wrapper wrapper = new DefaultWrapper();
                wrapper.setServletContext(applicationContext);
                wrapper.setServletObj((ServletType) obj.getValue());
                wrapper.setParent(this);
                wrapper.init();
                addChild(wrapper);
            }
            if(obj.getDeclaredType() == ListenerType.class) {
                Class<EventListener> listenerClass = (Class<EventListener>) ConfigUtils.loader.loadClass(((ListenerType) obj.getValue()).getListenerClass().getValue());
                eventListeners.add(listenerClass.newInstance());
            }
            if(obj.getDeclaredType() == ServletMappingType.class) {
                mapper.addServletMapping((ServletMappingType) obj.getValue());
            }
        }
    }

    @Override
    public void setWebAppObj(WebAppType webAppObj) {
        this.webAppObj = webAppObj;
    }
}
