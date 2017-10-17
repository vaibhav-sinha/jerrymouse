package com.github.vaibhavsinha.jerrymouse;

import com.github.vaibhavsinha.jerrymouse.impl.connector.DefaultHttpConnector;
import com.github.vaibhavsinha.jerrymouse.impl.container.DefaultContext;
import com.github.vaibhavsinha.jerrymouse.model.api.Connector;
import com.github.vaibhavsinha.jerrymouse.model.api.Container;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by vaibhav on 13/10/17.
 */
@Slf4j
public class JerryMouse {

    private Container container;
    private Connector connector;


    public static void main(String[] args) throws Exception {
        JerryMouse jerryMouse = new JerryMouse();
        jerryMouse.setupShutdownHook();
        jerryMouse.run();
    }

    private void run() throws Exception {
        container = new DefaultContext();
        container.setName("Root Context");
        ((DefaultContext) container).setWebAppObj(ConfigUtils.webApp);
        ((DefaultContext) container).setDocBase("ROOT");
        ((DefaultContext) container).setContextPath("/");
        container.start();

        connector = new DefaultHttpConnector();
        connector.setContainer(container);
        connector.initialize();
        connector.start();
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                container.stop();
                connector.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
