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

import io.github.tesla.filter.RequestFilterTypeEnum;
import io.github.tesla.gateway.cache.ApiAndFilterCacheComponent;
import io.github.tesla.gateway.config.SpringContextHolder;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author liushiming
 * @version SpringCloudHttpRequestFilter.java, v 0.0.1 2018年5月4日 下午2:53:00 liushiming
 */
public class SpringCloudHttpRequestFilter extends HttpRequestFilter {

  private final DynamicSpringCloudClient springCloudClient =
      SpringContextHolder.getBean(DynamicSpringCloudClient.class);

  private final ApiAndFilterCacheComponent routeRuleCache =
      SpringContextHolder.getBean(ApiAndFilterCacheComponent.class);

  @Override
  public HttpResponse doFilter(HttpRequest originalRequest, HttpObject httpObject,
      ChannelHandlerContext channelHandlerContext) {
    return null;
  }

  @Override
  public RequestFilterTypeEnum filterType() {
    return null;
  }

}
