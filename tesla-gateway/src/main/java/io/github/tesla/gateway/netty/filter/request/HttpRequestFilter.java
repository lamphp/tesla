package io.github.tesla.gateway.netty.filter.request;

import io.github.tesla.common.RequestFilterTypeEnum;
import io.github.tesla.gateway.netty.filter.AbstractCommonFilter;
import io.github.tesla.gateway.netty.servlet.NettyHttpServletRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;


public abstract class HttpRequestFilter extends AbstractCommonFilter {

  /**
   * servletRequest 拷贝出来的HttRequest，可以重复操作
   */
  public abstract HttpResponse doFilter(NettyHttpServletRequest servletRequest,
      HttpObject httpObject, ChannelHandlerContext channelHandlerContext);

  public abstract RequestFilterTypeEnum filterType();

  @Override
  public String filterName() {
    return filterType().name();
  }

}
