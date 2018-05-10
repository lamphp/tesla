package io.github.tesla.gateway.netty.servlet;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import io.netty.buffer.ByteBuf;


public class ChannelBufferServletOutputStream extends ServletOutputStream {

  private final ByteBuf byteBuf;

  public ChannelBufferServletOutputStream(ByteBuf byteBuf) {
    if (byteBuf == null) {
      throw new NullPointerException("buffer");
    }
    this.byteBuf = byteBuf;
  }

  @Override
  public void write(int b) throws IOException {
    byteBuf.writeByte((byte) b);
  }


}
