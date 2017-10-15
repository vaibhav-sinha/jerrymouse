package com.github.vaibhavsinha.jerrymouse.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vaibhavsinha.jerrymouse.model.Config;
import com.github.vaibhavsinha.jerrymouse.model.descriptor.WebAppType;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vaibhav on 13/10/17.
 */
@Slf4j
public class ConfigUtils {

    public final static String home = System.getenv("JERRYMOUSE_HOME");
    public final static Config config;
    public final static WebAppType webApp;
    public final static ClassLoader loader;

    static {
        try {
            config = getConfig();
            webApp = getWebApp();
            loader = getUrlClassLoader();
        }
        catch (IOException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static Config getConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(home + "/conf/server.json"), Config.class);
    }

    private static WebAppType getWebApp() throws JAXBException {
        File file = new File(home + "/webapps/ROOT/WEB-INF/web.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(WebAppType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (WebAppType) unmarshaller.unmarshal(file);
    }

    private static URLClassLoader getUrlClassLoader() throws IOException {
        List<URL> urlList = new ArrayList<>();
        File classpath1 = new File(ConfigUtils.home + "/webapps/ROOT/WEB-INF/classes/");
        String repository1 = new URL("file", null, classpath1.getCanonicalPath() + File.separator).toString();
        urlList.add(new URL(null, repository1));

        File lib = new File(ConfigUtils.home + "/webapps/ROOT/WEB-INF/lib/");
        List<URL> jars = Arrays.stream(lib.listFiles()).filter(File::isFile).map(file -> {
            String repository2 = null;
            try {
                repository2 = new URL("file", null, file.getCanonicalPath()).toString();
                return new URL(null, repository2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        urlList.addAll(jars);

        return new URLClassLoader(urlList.toArray(new URL[0]));
    }


}
