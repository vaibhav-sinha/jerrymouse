package com.github.vaibhavsinha.jerrymouse.impl.connector;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Created by vaibhav on 14/10/17.
 */
public class ByteBufWriter extends Writer {

    private ByteBuf byteBuf;

    public ByteBufWriter(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        byteBuf.writeBytes(new String(cbuf).getBytes(StandardCharsets.UTF_8), off, len);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
