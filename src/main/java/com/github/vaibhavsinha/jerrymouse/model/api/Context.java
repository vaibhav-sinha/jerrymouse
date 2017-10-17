package com.github.vaibhavsinha.jerrymouse.model.api;

import com.github.vaibhavsinha.jerrymouse.model.descriptor.WebAppType;

import javax.servlet.ServletContext;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Context extends Container {

    void setWebAppObj(WebAppType webAppObj);
    void setDocBase(String docBase);
    void setContextPath(String contextPath);
    String getDocBase();
    String getContextPath();
    ClassLoader getClassLoader();
    void reload();
    ServletContext getServletContext();
    Manager getManager();
}
