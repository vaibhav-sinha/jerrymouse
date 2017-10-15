package com.github.vaibhavsinha.jerrymouse.model;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vaibhav on 14/10/17.
 */
public class JerryMouseServletRequest implements HttpServletRequest {

    private FullHttpRequest fullHttpRequest;
    private String pathInfo;
    private String servletPath;


    public JerryMouseServletRequest() {

    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public JerryMouseServletRequest(FullHttpRequest request) {
        this.fullHttpRequest = request;
    }


    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return ServerCookieDecoder.STRICT.decode(fullHttpRequest.headers().get(HttpHeaderNames.COOKIE)).stream().map(c -> new Cookie(c.name(), c.value())).collect(Collectors.toList()).toArray(new Cookie[0]);
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return fullHttpRequest.headers().get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(fullHttpRequest.headers().getAll(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(fullHttpRequest.headers().names());
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return fullHttpRequest.method().name();
    }

    @Override
    public String getPathInfo() {
        return pathInfo;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return "/";
    }

    @Override
    public String getQueryString() {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(getRequestURI());
        return queryStringDecoder.path();
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return fullHttpRequest.uri();
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(fullHttpRequest.uri());
    }

    @Override
    public String getServletPath() {
        return servletPath.endsWith("/") ? servletPath.substring(0, servletPath.length() - 1) : servletPath;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return fullHttpRequest.content().readableBytes();
    }

    @Override
    public long getContentLengthLong() {
        return (long) getContentLength();
    }

    @Override
    public String getContentType() {
        return fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE.toString());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ByteBufServletInputStream(fullHttpRequest.content());
    }

    @Override
    public String getParameter(String name) {
        return getParameterMap().get(name)[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return getParameterMap().get(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(getRequestURI());
        Map<String, String[]> parameterMap = new HashMap<>();
        for(Map.Entry<String, List<String>> entry : queryStringDecoder.parameters().entrySet()) {
            parameterMap.put(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
        return parameterMap;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }
}
