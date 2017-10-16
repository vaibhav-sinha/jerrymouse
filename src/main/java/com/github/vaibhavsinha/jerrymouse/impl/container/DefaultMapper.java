package com.github.vaibhavsinha.jerrymouse.impl.container;

import com.github.vaibhavsinha.jerrymouse.impl.connector.DefaultConnectorServletRequest;
import com.github.vaibhavsinha.jerrymouse.model.api.Container;
import com.github.vaibhavsinha.jerrymouse.model.api.Mapper;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletMappingType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.UrlPatternType;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;

import javax.servlet.ServletRequest;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 16/10/17.
 */
public class DefaultMapper implements Mapper {

    private Container container;
    private List<ServletMappingType> mappingTypeList = new ArrayList<>();

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Container map(ServletRequest request) {
        DefaultConnectorServletRequest servletRequest = (DefaultConnectorServletRequest) request;
        String url = servletRequest.getRequestURI().split("\\?")[0];
        String pathInfo = isValidUrl(url);
        if(pathInfo != null) {
            String matchedUrl = url.replace(pathInfo, "");
            servletRequest.setPathInfo(pathInfo);
            servletRequest.setServletPath(matchedUrl);
            String servletName = getServletNameByUrl(matchedUrl);
            for(Container child : container.findChildren()) {
                if(child.getName().equals(servletName)) {
                    return child;
                }
            }
        }
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void addServletMapping(ServletMappingType servletMappingType) {
        mappingTypeList.add(servletMappingType);
    }

    private String getServletNameByUrl(String url) {
        for(ServletMappingType smt : mappingTypeList) {
            for (UrlPatternType upt : smt.getUrlPattern()) {
                if (url.startsWith(upt.getValue())) {
                    return smt.getServletName().getValue();
                }
            }
        }

        return null;
    }

    private String isValidUrl(String url) {
        for(ServletMappingType smt : mappingTypeList) {
                for(UrlPatternType patternType : smt.getUrlPattern()) {
                    if(url.startsWith(patternType.getValue())) {
                        return url.replace(patternType.getValue(), "");
                    }
                }
        }
        return null;
    }
}
