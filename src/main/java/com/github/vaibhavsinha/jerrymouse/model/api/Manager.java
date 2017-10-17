package com.github.vaibhavsinha.jerrymouse.model.api;

import com.github.vaibhavsinha.jerrymouse.impl.manager.Session;

import java.io.IOException;

/**
 * Created by vaibhav on 17/10/17.
 */
public interface Manager extends Lifecycle {
    public Container getContainer();
    public void setContainer(Container container);
    public int getMaxInactiveInterval();
    public void setMaxInactiveInterval(int interval);
    public void add(Session session);
    public Session createSession();
    public Session findSession(String id) throws IOException;
    public Session[] findSessions();
    public void load() throws ClassNotFoundException, IOException;
    public void remove(Session session);
    public void unload() throws IOException;
}
