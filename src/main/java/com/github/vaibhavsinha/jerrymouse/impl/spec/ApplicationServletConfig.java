package com.github.vaibhavsinha.jerrymouse.impl.spec;

import com.github.vaibhavsinha.jerrymouse.model.descriptor.ParamValueType;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vaibhav on 15/10/17.
 */
public class ApplicationServletConfig implements ServletConfig {

    private String name;
    private List<ParamValueType> paramValueTypeList;
    private ServletContext servletContext;

    public void setName(String name) {
        this.name = name;
    }

    public void setParamValueTypeList(List<ParamValueType> paramValueTypeList) {
        this.paramValueTypeList = paramValueTypeList;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public String getServletName() {
        return name;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        ParamValueType paramValueType = paramValueTypeList.stream().filter(p -> p.getParamName().toString().equals(name)).findFirst().orElse(null);
        if(paramValueType != null) {
            return paramValueType.getParamValue().toString();
        }
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(paramValueTypeList.stream().map(paramValueType -> paramValueType.getParamName().toString()).collect(Collectors.toList()));
    }
}
