package com.github.vaibhavsinha.jerrymouse.model.api;

import com.github.vaibhavsinha.jerrymouse.model.descriptor.WebAppType;

/**
 * Created by vaibhav on 16/10/17.
 */
public interface Context extends Container {

    void setWebAppObj(WebAppType webAppObj);
}
