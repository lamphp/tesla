/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tesla.gateway.netty.filter.request;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.tesla.filter.RequestFilterTypeEnum;
import io.github.tesla.gateway.netty.filter.FilterUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * ip黑名单过滤
 */
public class BlackIpHttpRequesFilter extends HttpRequestFilter {

  public static HttpRequestFilter newFilter() {
    return new BlackIpHttpRequesFilter();
  }

  @Override
  public HttpResponse doFilter(HttpRequest originalRequest, HttpObject httpObject,
      ChannelHandlerContext channelHandlerContext) {
    if (httpObject instanceof FullHttpRequest) {
      FullHttpRequest httpRequest = (FullHttpRequest) httpObject;
      String realIp = FilterUtil.getRealIp(httpRequest, channelHandlerContext);
      if (realIp != null) {
        List<Pattern> patterns = super.getCommonRule(this);
        for (Pattern pattern : patterns) {
          Matcher matcher = pattern.matcher(realIp.toLowerCase());
          if (matcher.find()) {
            super.writeFilterLog(realIp, BlackIpHttpRequesFilter.class, pattern.pattern());
            return super.createResponse(HttpResponseStatus.FORBIDDEN, originalRequest);
          }
        }
      }
    }
    return null;
  }

  @Override
  public RequestFilterTypeEnum filterType() {
    return RequestFilterTypeEnum.BlackIpHttpRequesFilter;
  }



}
