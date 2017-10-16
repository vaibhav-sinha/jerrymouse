package com.github.vaibhavsinha.jerrymouse.impl.container;

import com.github.vaibhavsinha.jerrymouse.ApplicationContext;
import com.github.vaibhavsinha.jerrymouse.impl.connector.DefaultConnectorServletResponse;
import com.github.vaibhavsinha.jerrymouse.model.api.*;
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
import java.util.Arrays;
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
    private boolean started = false;


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
    public void setWebAppObj(WebAppType webAppObj) {
        this.webAppObj = webAppObj;
    }

    @Override
    public void start() throws LifecycleException {
        if(started) {
            throw new LifecycleException(new Throwable("Container already started"));
        }
        lifecycleSupport.fireLifecycleEvent(Lifecycle.BEFORE_START_EVENT, null);
        started = true;
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
                wrapper.start();
                addChild(wrapper);
            }
            if(obj.getDeclaredType() == ListenerType.class) {
                try {
                    Class<EventListener> listenerClass = (Class<EventListener>) ConfigUtils.loader.loadClass(((ListenerType) obj.getValue()).getListenerClass().getValue());
                    eventListeners.add(listenerClass.newInstance());
                } catch (Exception e) {
                    throw new LifecycleException(e);
                }
            }
            if(obj.getDeclaredType() == ServletMappingType.class) {
                mapper.addServletMapping((ServletMappingType) obj.getValue());
            }
        }
        lifecycleSupport.fireLifecycleEvent(Lifecycle.START_EVENT, null);
        lifecycleSupport.fireLifecycleEvent(Lifecycle.AFTER_START_EVENT, null);
    }

    @Override
    public void stop() throws LifecycleException {
        if(!started) {
            throw new LifecycleException(new Throwable("Container not started"));
        }
        lifecycleSupport.fireLifecycleEvent(Lifecycle.BEFORE_STOP_EVENT, null);
        started = false;
        mapper.stop();
        for(Container child : findChildren()) {
            child.stop();
        }
        lifecycleSupport.fireLifecycleEvent(Lifecycle.STOP_EVENT, null);
        lifecycleSupport.fireLifecycleEvent(Lifecycle.AFTER_STOP_EVENT, null);
    }
}
