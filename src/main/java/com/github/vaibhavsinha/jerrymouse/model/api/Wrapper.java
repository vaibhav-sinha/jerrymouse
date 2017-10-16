package com.github.vaibhavsinha.jerrymouse.model.api;

import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletType;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Wrapper extends Container {

    void setServletObj(ServletType servletObj);
    void setServletContext(ServletContext context);
    Servlet allocate() throws Exception;
    void load() throws Exception;
}
