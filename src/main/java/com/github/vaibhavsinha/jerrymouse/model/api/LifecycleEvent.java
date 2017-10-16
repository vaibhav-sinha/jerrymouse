package com.github.vaibhavsinha.jerrymouse.model.api;

import java.util.EventObject;

/**
 * Created by vaibhav on 16/10/17.
 */
public class LifecycleEvent extends EventObject {

    private Object data = null;
    private Lifecycle lifecycle = null;
    private String type = null;


    public LifecycleEvent(Lifecycle lifecycle, String type) {
        this(lifecycle, type, null);
    }

    public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
        super(lifecycle);
        this.lifecycle = lifecycle;
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return (this.data);
    }

    public Lifecycle getLifecycle() {
        return (this.lifecycle);
    }

    public String getType() {
        return (this.type);
    }
}
