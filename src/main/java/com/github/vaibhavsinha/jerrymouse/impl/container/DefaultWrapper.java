package com.github.vaibhavsinha.jerrymouse.impl.container;

import com.github.vaibhavsinha.jerrymouse.impl.connector.DefaultConnectorServletRequest;
import com.github.vaibhavsinha.jerrymouse.impl.manager.Session;
import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletConfig;
import com.github.vaibhavsinha.jerrymouse.model.api.Context;
import com.github.vaibhavsinha.jerrymouse.model.api.Lifecycle;
import com.github.vaibhavsinha.jerrymouse.model.api.LifecycleException;
import com.github.vaibhavsinha.jerrymouse.model.api.Wrapper;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletType;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by vaibhav on 16/10/17.
 */
public class DefaultWrapper extends DefaultAbstractContainer implements Wrapper {

    private ServletType servletObj;
    private Context context;
    private Servlet instance;
    private Boolean started = false;

    @Override
    public void setServletObj(ServletType servletObj) {
        this.servletObj = servletObj;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Servlet allocate() throws Exception {
        if(instance == null) {
            load();
        }
        return instance;
    }

    @Override
    public void load() throws Exception {
        if(instance == null) {
            Class<Servlet> servletClass = (Class<Servlet>) ((Context) parent).getClassLoader().loadClass(servletObj.getServletClass().getValue());
            instance = servletClass.newInstance();
            JerryMouseServletConfig config = new JerryMouseServletConfig();
            config.setName(servletObj.getServletName().getValue());
            config.setParamValueTypeList(servletObj.getInitParam());
            config.setServletContext(context.getServletContext());
            instance.init(config);
        }
    }

    @Override
    public void invoke(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        ((DefaultConnectorServletRequest)request).setContext(context);
        instance.service(request, response);
        HttpSession session = ((DefaultConnectorServletRequest) request).getSession(false);
        if(session != null) {
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            ((HttpServletResponse) response).addCookie(cookie);
        }
    }

    @Override
    public void start() throws LifecycleException {
        if(started) {
            throw new LifecycleException(new Throwable("Container already started"));
        }
        lifecycleSupport.fireLifecycleEvent(Lifecycle.BEFORE_START_EVENT, null);
        started = true;
        setName(servletObj.getServletName().getValue());
        if(servletObj.getLoadOnStartup().equals("1")) {
            try {
                load();
            } catch (Exception e) {
                throw new LifecycleException(e);
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
        if(instance != null) {
            instance.destroy();
        }
        lifecycleSupport.fireLifecycleEvent(Lifecycle.STOP_EVENT, null);
        lifecycleSupport.fireLifecycleEvent(Lifecycle.AFTER_STOP_EVENT, null);
    }
}
