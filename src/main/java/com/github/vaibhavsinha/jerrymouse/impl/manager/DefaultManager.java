package com.github.vaibhavsinha.jerrymouse.impl.manager;

import com.github.vaibhavsinha.jerrymouse.model.api.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created by vaibhav on 17/10/17.
 */
@Slf4j
public class DefaultManager extends TimerTask implements Manager {

    private Context container;
    private Long maxInactiveInterval;
    private Map<String, Session> sessionMap = new HashMap<>();
    private Timer timer;

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = (Context) container;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval.intValue();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = (long) interval;
    }

    @Override
    public void add(Session session) {
        sessionMap.put(session.getId(), session);
    }

    @Override
    public Session createSession() {
        Session session = new Session(container, UUID.randomUUID().toString());
        session.setMaxInactiveInterval(maxInactiveInterval.intValue());
        sessionMap.put(session.getId(), session);
        return session;
    }

    @Override
    public Session findSession(String id) throws IOException {
        return sessionMap.values().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Session[] findSessions() {
        return sessionMap.values().toArray(new Session[0]);
    }

    @Override
    public void load() throws ClassNotFoundException, IOException {

    }

    @Override
    public void remove(Session session) {
        sessionMap.remove(session.getId());
    }

    @Override
    public void unload() throws IOException {

    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void start() throws LifecycleException {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(this, 0, maxInactiveInterval);
    }

    @Override
    public void stop() throws LifecycleException {
        timer.cancel();
    }

    @Override
    public void run() {
        log.debug("Cleaning sessions");
        List<String> ids = new ArrayList<>(sessionMap.keySet());
        ids.forEach(id -> {
            if(!sessionMap.get(id).isValid()) {
                sessionMap.get(id).invalidate();
            }
        });
    }
}
