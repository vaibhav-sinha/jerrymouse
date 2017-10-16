package com.github.vaibhavsinha.jerrymouse.impl.connector;

import io.netty.buffer.ByteBuf;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

/**
 * Created by vaibhav on 14/10/17.
 */
public class ByteBufServletOutputStream extends ServletOutputStream {

    private ByteBuf byteBuf;

    public ByteBufServletOutputStream(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    @Override
    public void write(int b) throws IOException {
        byteBuf.writeByte(b);
    }
}
