package com.github.vaibhavsinha.jerrymouse.model.api;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Loader extends Lifecycle {

    public ClassLoader getClassLoader();
    public Container getContainer();
    public void setContainer(Container container);
    public boolean getReloadable();
    public void setReloadable(boolean reloadable);
    public void addRepository(String repository);
    public String[] findRepositories();
    public boolean modified();
}
