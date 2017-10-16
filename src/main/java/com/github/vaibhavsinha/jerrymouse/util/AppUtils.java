package com.github.vaibhavsinha.jerrymouse.util;

/**
 * Created by vaibhav on 16/10/17.
 */
public class AppUtils {

    public static <T> boolean isInstanceOf(Class<T> clazz, Class<T> targetClass) {
        return clazz.isInstance(targetClass);
    }
}
