package com.github.vaibhavsinha.jerrymouse.model.api;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Connector {

    void initialize() throws Exception;
    void start() throws Exception;
    void stop() throws Exception;
    boolean isAvailable();
    Container getContainer();
    void setContainer(Container container);
}
