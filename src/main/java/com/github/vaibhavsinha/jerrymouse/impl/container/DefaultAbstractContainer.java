package com.github.vaibhavsinha.jerrymouse.impl.container;

import com.github.vaibhavsinha.jerrymouse.model.api.Container;
import com.github.vaibhavsinha.jerrymouse.model.api.Lifecycle;
import com.github.vaibhavsinha.jerrymouse.model.api.LifecycleListener;
import com.github.vaibhavsinha.jerrymouse.model.api.LifecycleSupport;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 16/10/17.
 */
public abstract class DefaultAbstractContainer implements Container {

    protected String name;
    protected Container parent;
    protected List<Container> children = new ArrayList<>();
    protected LifecycleSupport lifecycleSupport = new LifecycleSupport(this);


    @Override
    public void addChild(Container child) {
        children.add(child);
    }

    @Override
    public void removeChild(Container child) {
        children.remove(child);
    }

    @Override
    public Container findChild(String name) {
        return children.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public Container[] findChildren() {
        return children.toArray(new Container[0]);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleSupport.addLifecycleListener(listener);
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return lifecycleSupport.findLifecycleListeners();
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleSupport.removeLifecycleListener(listener);
    }

    public void fireLifecycleEvent(String type, Object data) {
        lifecycleSupport.fireLifecycleEvent(type, data);
    }
}
