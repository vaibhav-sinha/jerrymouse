package com.github.vaibhavsinha.jerrymouse.model.api;

import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletMappingType;

import javax.servlet.ServletRequest;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Mapper extends Lifecycle {

    Container getContainer();
    void setContainer(Container container);
    Container map(ServletRequest request);
    void addServletMapping(ServletMappingType servletMappingType);
}
