package com.github.vaibhavsinha.jerrymouse.impl.container;

import com.github.vaibhavsinha.jerrymouse.impl.connector.DefaultConnectorServletRequest;
import com.github.vaibhavsinha.jerrymouse.impl.spec.ApplicationFilterChain;
import com.github.vaibhavsinha.jerrymouse.impl.spec.ApplicationFilterConfig;
import com.github.vaibhavsinha.jerrymouse.impl.spec.ApplicationServletConfig;
import com.github.vaibhavsinha.jerrymouse.model.api.Context;
import com.github.vaibhavsinha.jerrymouse.model.api.Lifecycle;
import com.github.vaibhavsinha.jerrymouse.model.api.LifecycleException;
import com.github.vaibhavsinha.jerrymouse.model.api.Wrapper;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.FilterMappingType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.FilterType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.ServletType;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.UrlPatternType;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vaibhav on 16/10/17.
 */
public class DefaultWrapper extends DefaultAbstractContainer implements Wrapper {

    private ServletType servletObj;
    private List<FilterType> filterObjs;
    private List<FilterMappingType> filterMappingObjs;
    private Context context;
    private Servlet instance;
    private Map<String, Filter> filters = new HashMap<>();
    private Boolean started = false;

    @Override
    public void setFilterObjs(List<FilterType> filterObjs) {
        this.filterObjs = filterObjs;
    }

    @Override
    public void setFilterMappingObjs(List<FilterMappingType> filterMappingObjs) {
        this.filterMappingObjs = filterMappingObjs;
    }

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
            ApplicationServletConfig config = new ApplicationServletConfig();
            config.setName(servletObj.getServletName().getValue());
            config.setParamValueTypeList(servletObj.getInitParam());
            config.setServletContext(context.getServletContext());
            instance.init(config);

            for(FilterType filterObj : filterObjs) {
                Class<Filter> filterClass = (Class<Filter>) ((Context) parent).getClassLoader().loadClass(filterObj.getFilterClass().getValue());
                Filter filter = filterClass.newInstance();
                ApplicationFilterConfig filterConfig = new ApplicationFilterConfig();
                filterConfig.setName(filterObj.getFilterName().getValue());
                filterConfig.setParamValueTypeList(filterObj.getInitParam());
                filterConfig.setServletContext(context.getServletContext());
                filter.init(filterConfig);
                filters.put(filterConfig.getFilterName(), filter);
            }
        }
    }

    @Override
    public void invoke(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        ((DefaultConnectorServletRequest)request).setContext(context);
        getFilterChain((HttpServletRequest) request).doFilter(request, response);
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

    private FilterChain getFilterChain(HttpServletRequest request) {
        ApplicationFilterChain filterChain = new ApplicationFilterChain();
        filterChain.setServlet(instance);
        List<Filter> chainFilters = new ArrayList<>();
        String uriToMatch = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        uriToMatch = uriToMatch.startsWith("/") ? uriToMatch : "/" + uriToMatch;
        for(FilterMappingType fmt : filterMappingObjs) {
            for(Object url : fmt.getUrlPatternOrServletName()) {
                UrlPatternType upt = (UrlPatternType) url;
                if(uriToMatch.startsWith(upt.getValue())) {
                    Filter matching = getFilterByName(fmt.getFilterName().getValue());
                    if(!chainFilters.contains(matching)) {
                        chainFilters.add(matching);
                    }
                }
            }
        }
        filterChain.setFilters(chainFilters);
        return filterChain;
    }

    private Filter getFilterByName(String name) {
        Map.Entry<String, Filter> entry = filters.entrySet().stream().filter(e -> e.getKey().equals(name)).findFirst().orElse(null);
        if(entry == null) {
            throw new RuntimeException("Filter " + name + " specified in filter mapping is not defined");
        }
        return entry.getValue();
    }
}
