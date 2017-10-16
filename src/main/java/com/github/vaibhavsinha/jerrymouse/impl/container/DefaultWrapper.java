package com.github.vaibhavsinha.jerrymouse.impl.container;

import com.github.vaibhavsinha.jerrymouse.model.JerryMouseServletConfig;
import com.github.vaibhavsinha.jerrymouse.model.api.Wrapper;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletType;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by vaibhav on 16/10/17.
 */
public class DefaultWrapper extends DefaultAbstractContainer implements Wrapper {

    private ServletType servletObj;
    private ServletContext servletContext;
    private Servlet instance;

    @Override
    public void setServletObj(ServletType servletObj) {
        this.servletObj = servletObj;
    }

    @Override
    public void setServletContext(ServletContext context) {
        this.servletContext = context;
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
            Class<Servlet> servletClass = (Class<Servlet>) ConfigUtils.loader.loadClass(servletObj.getServletClass().getValue());
            instance = servletClass.newInstance();
            JerryMouseServletConfig config = new JerryMouseServletConfig();
            config.setName(servletObj.getServletName().getValue());
            config.setParamValueTypeList(servletObj.getInitParam());
            config.setServletContext(servletContext);
            instance.init(config);
        }
    }

    @Override
    public void invoke(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        instance.service(request, response);
    }

    @Override
    public void init() throws Exception {
        setName(servletObj.getServletName().getValue());
        if(servletObj.getLoadOnStartup().equals("1")) {
            load();
        }
    }

}
