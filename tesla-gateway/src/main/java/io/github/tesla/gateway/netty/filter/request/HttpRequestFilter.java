package io.github.tesla.gateway.netty.filter.request;

import io.github.tesla.common.RequestFilterTypeEnum;
import io.github.tesla.gateway.netty.filter.AbstractCommonFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;


public abstract class HttpRequestFilter extends AbstractCommonFilter {

  public abstract HttpResponse doFilter(HttpRequest originalRequest, HttpObject httpObject,
      ChannelHandlerContext channelHandlerContext);

  public abstract RequestFilterTypeEnum filterType();

  @Override
  public String filterName() {
    return filterType().name();
  }

}
