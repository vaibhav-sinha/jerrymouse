package com.github.vaibhavsinha.jerrymouse.model.api;

import com.github.vaibhavsinha.jerrymouse.model.descriptor.FilterMappingType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.FilterType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletType;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.util.List;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Wrapper extends Container {

    void setFilterObjs(List<FilterType> filterObjs);
    void setFilterMappingObjs(List<FilterMappingType> filterMappingObjs);
    void setServletObj(ServletType servletObj);
    void setContext(Context context);
    Servlet allocate() throws Exception;
    void load() throws Exception;
}
