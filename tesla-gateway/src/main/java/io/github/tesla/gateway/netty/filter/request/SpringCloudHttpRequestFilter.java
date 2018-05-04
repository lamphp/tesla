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

import java.net.URI;

import org.apache.commons.lang3.tuple.Pair;

import io.github.tesla.filter.RequestFilterTypeEnum;
import io.github.tesla.filter.domain.ApiSpringCloudDO;
import io.github.tesla.gateway.cache.ApiAndFilterCacheComponent;
import io.github.tesla.gateway.config.SpringContextHolder;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
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

  private final ApiAndFilterCacheComponent apiCache =
      SpringContextHolder.getBean(ApiAndFilterCacheComponent.class);


  public static HttpRequestFilter newFilter() {
    return new SpringCloudHttpRequestFilter();
  }

  @Override
  public HttpResponse doFilter(HttpRequest originalRequest, HttpObject httpObject,
      ChannelHandlerContext channelHandlerContext) {
    if (httpObject instanceof FullHttpRequest && springCloudClient != null) {
      FullHttpRequest httpRequest = (FullHttpRequest) httpObject;
      String actorPath = httpRequest.uri();
      int index = actorPath.indexOf("?");
      if (index > -1) {
        actorPath = actorPath.substring(0, index);
      }
      Pair<String, ApiSpringCloudDO> springCloudPair = apiCache.getSpringCloudRoute(actorPath);
      if (springCloudPair != null) {
        String changedPath = springCloudPair.getLeft();
        ApiSpringCloudDO springCloudDo = springCloudPair.getRight();
        URI loadbalanceHostAndPort = springCloudClient.loadBalanceCall(springCloudDo);
        httpRequest.setUri(changedPath);
        httpRequest.headers().set(HttpHeaderNames.HOST, buildHost(loadbalanceHostAndPort));
      } else {
        return null;
      }
    }
    return null;
  }

  private String buildHost(URI uri) {
    if (uri.getPort() != -1) {
      return uri.getHost() + ":" + uri.getPort();
    } else {
      return uri.getHost();
    }
  }

  @Override
  public RequestFilterTypeEnum filterType() {
    return RequestFilterTypeEnum.SPRINGCLOUD;
  }

}
