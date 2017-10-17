package com.github.vaibhavsinha.jerrymouse.impl.loader;

import com.github.vaibhavsinha.jerrymouse.model.api.*;
import com.github.vaibhavsinha.jerrymouse.util.ConfigUtils;
import com.github.vaibhavsinha.jerrymouse.util.FileWatcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vaibhav on 16/10/17.
 */
public class WebappLoader implements Loader {

    private Boolean started = false;
    private Context container;
    private Boolean reloadable = false;
    private List<String> repositories = new ArrayList<>();
    private LifecycleSupport lifecycleSupport = new LifecycleSupport(this);
    private ClassLoader classLoader;
    private FileWatcher fileWatcher;


    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = (Context) container;
    }

    @Override
    public boolean getReloadable() {
        return reloadable;
    }

    @Override
    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    @Override
    public void addRepository(String repository) {
        repositories.add(repository);
    }

    @Override
    public String[] findRepositories() {
        return repositories.toArray(new String[0]);
    }

    @Override
    public boolean modified() {
        return false;
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

    @Override
    public void start() throws LifecycleException {
        try {
            if(started) {
                throw new LifecycleException(new Throwable("Container already started"));
            }
            lifecycleSupport.fireLifecycleEvent(Lifecycle.BEFORE_START_EVENT, null);
            String basePath = ConfigUtils.home + "/webapps/" + container.getDocBase() + "/WEB-INF";
            List<URL> urlList = new ArrayList<>();
            File classpath1 = new File(basePath + "/classes/");
            String repository1 = new URL("file", null, classpath1.getCanonicalPath() + File.separator).toString();
            urlList.add(new URL(null, repository1));

            File lib = new File(basePath + "/lib/");
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

            classLoader = new URLClassLoader(urlList.toArray(new URL[0]));
            fileWatcher = new FileWatcher(new File(basePath + "/web.xml"), container);
            fileWatcher.start();
            started = true;
            lifecycleSupport.fireLifecycleEvent(Lifecycle.START_EVENT, null);
            lifecycleSupport.fireLifecycleEvent(Lifecycle.AFTER_START_EVENT, null);
        } catch (Exception e) {
            throw new LifecycleException(e);
        }
    }

    @Override
    public void stop() throws LifecycleException {
        if(!started) {
            throw new LifecycleException(new Throwable("Container not started"));
        }
        lifecycleSupport.fireLifecycleEvent(Lifecycle.BEFORE_STOP_EVENT, null);
        fileWatcher.stopThread();
        started = false;
        lifecycleSupport.fireLifecycleEvent(Lifecycle.STOP_EVENT, null);
        lifecycleSupport.fireLifecycleEvent(Lifecycle.AFTER_STOP_EVENT, null);
    }
}
