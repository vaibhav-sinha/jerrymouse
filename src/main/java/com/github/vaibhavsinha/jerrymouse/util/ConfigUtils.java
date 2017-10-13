package com.github.vaibhavsinha.jerrymouse.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vaibhavsinha.jerrymouse.model.Config;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Created by vaibhav on 13/10/17.
 */
@Slf4j
public class ConfigUtils {

    public final static String home = System.getenv("JERRYMOUSE_HOME");
    public final static Config config;

    static {
        try {
            config = getConfig();
        }
        catch (IOException e) {
            log.error("Could not read config from file. Error: " , e);
            throw new RuntimeException(e);
        }
    }

    private static Config getConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(home + "/conf/server.json"), Config.class);
    }
}
