package com.github.vaibhavsinha.jerrymouse.impl.manager;

import com.github.vaibhavsinha.jerrymouse.model.api.Context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.*;

/**
 * Created by vaibhav on 17/10/17.
 */
public class Session implements HttpSession, Serializable {

    private Context context;
    private Date createdAt;
    private Date lastAccessedAt;
    private String id;
    private Map<String, Object> data = new HashMap<>();
    private Long maxInactiveInterval;

    public Session(Context context, String id) {
        this.context = context;
        this.id = id;
        createdAt = new Date();
        lastAccessedAt = new Date();
    }

    public boolean isValid() {
        return getLastAccessedTime() > new Date().getTime() - maxInactiveInterval;
    }

    public void access() {
        lastAccessedAt = new Date();
    }

    @Override
    public long getCreationTime() {
        return createdAt.getTime();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedAt.getTime();
    }

    @Override
    public ServletContext getServletContext() {
        return context.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = (long) interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval.intValue();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return data.get(name);
    }

    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(data.keySet());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {
        data.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeAttribute(String name) {
        data.remove(name);
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {
        data = new HashMap<>();
        context.getManager().remove(this);
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
