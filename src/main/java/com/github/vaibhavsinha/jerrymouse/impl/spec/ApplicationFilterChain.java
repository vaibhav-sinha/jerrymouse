package com.github.vaibhavsinha.jerrymouse.impl.spec;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;

/**
 * Created by vaibhav on 18/10/17.
 */
public class ApplicationFilterChain implements FilterChain {

    private Servlet servlet;
    private List<Filter> filters;
    private int index = 0;


    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if(index == filters.size()) {
            servlet.service(request, response);
        }
        else {
            int currentIndex = index;
            index++;
            filters.get(currentIndex).doFilter(request, response, this);
        }
    }
}
