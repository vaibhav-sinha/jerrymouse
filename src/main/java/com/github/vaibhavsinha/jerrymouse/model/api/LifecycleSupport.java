package com.github.vaibhavsinha.jerrymouse.model.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 16/10/17.
 */
public class LifecycleSupport {

    private Lifecycle lifecycle;
    private List<LifecycleListener> lifecycleListeners = new ArrayList<>();

    public LifecycleSupport(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    public LifecycleListener[] findLifecycleListeners() {
        return lifecycleListeners.toArray(new LifecycleListener[0]);
    }

    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    public void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        lifecycleListeners.forEach(lifecycleListener -> lifecycleListener.lifecycleEvent(event));
    }

}
