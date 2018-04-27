package io.github.tesla.gateway.netty.filter.response;

import io.github.tesla.filter.ResponseFilterTypeEnum;
import io.github.tesla.gateway.netty.filter.AbstractCommonFilter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;


public abstract class HttpResponseFilter extends AbstractCommonFilter {

  public abstract HttpResponse doFilter(HttpRequest originalRequest, HttpResponse httpResponse);

  public abstract ResponseFilterTypeEnum filterType();

  @Override
  public String filterName() {
    return filterType().name();
  }
}
