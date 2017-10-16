package com.github.vaibhavsinha.jerrymouse.impl.connector;

import io.netty.buffer.ByteBuf;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * Created by vaibhav on 14/10/17.
 */
public class ByteBufServletInputStream extends ServletInputStream {

    private ByteBuf byteBuf;

    public ByteBufServletInputStream(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public boolean isFinished() {
        return byteBuf.readableBytes() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return byteBuf.readInt();
    }
}
