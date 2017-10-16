package com.github.vaibhavsinha.jerrymouse.model.api;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Container extends Lifecycle {

    void addChild(Container child);
    void removeChild(Container child);
    Container findChild(String name);
    Container[] findChildren();
    String getName();
    void setName(String name);
    Container getParent();
    void setParent(Container parent);
    void invoke(ServletRequest request, ServletResponse response) throws ServletException, IOException;
}
