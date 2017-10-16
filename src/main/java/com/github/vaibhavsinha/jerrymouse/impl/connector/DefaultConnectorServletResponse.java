package com.github.vaibhavsinha.jerrymouse.impl.connector;

import com.github.vaibhavsinha.jerrymouse.impl.connector.ByteBufServletOutputStream;
import com.github.vaibhavsinha.jerrymouse.impl.connector.ByteBufWriter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

/**
 * Created by vaibhav on 14/10/17.
 */
public class DefaultConnectorServletResponse implements HttpServletResponse {

    private FullHttpResponse fullHttpResponse;

    public DefaultConnectorServletResponse() {
        fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    }

    public FullHttpResponse getFullHttpResponse() {
        return fullHttpResponse;
    }


    @Override
    public void addCookie(Cookie cookie) {
        DefaultCookie defaultCookie = new DefaultCookie(cookie.getName(), cookie.getValue());
        fullHttpResponse.headers().add(HttpHeaderNames.SET_COOKIE.toString(), ServerCookieEncoder.STRICT.encode(defaultCookie));
    }

    @Override
    public boolean containsHeader(String name) {
        return fullHttpResponse.headers().contains(name);
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {
        fullHttpResponse.headers().set(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        fullHttpResponse.headers().add(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        fullHttpResponse.headers().set(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        fullHttpResponse.headers().add(name, value);
    }

    @Override
    public void setStatus(int sc) {
        fullHttpResponse.setStatus(HttpResponseStatus.valueOf(sc));
    }

    @Override
    public void setStatus(int sc, String sm) {
        fullHttpResponse.setStatus(new HttpResponseStatus(sc, sm));
    }

    @Override
    public int getStatus() {
        return fullHttpResponse.status().code();
    }

    @Override
    public String getHeader(String name) {
        return fullHttpResponse.headers().get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return fullHttpResponse.headers().getAll(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return fullHttpResponse.headers().names();
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ByteBufServletOutputStream(fullHttpResponse.content());
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new ByteBufWriter(fullHttpResponse.content()));
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {
        addIntHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), len);
    }

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public void setContentType(String type) {
        addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return fullHttpResponse.content().capacity();
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
