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

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import io.github.tesla.common.RequestFilterTypeEnum;
import io.github.tesla.gateway.config.SpringContextHolder;
import io.github.tesla.gateway.mapping.BodyMapping;
import io.github.tesla.gateway.mapping.HeaderMapping;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.github.tesla.gateway.utils.ProxyUtils;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author liushiming
 * @version DroolsRequestFilter.java, v 0.0.1 2018年5月2日 下午3:08:17 liushiming
 */
public class DroolsRequestFilter extends HttpRequestFilter {

  private static Logger LOG = LoggerFactory.getLogger(DroolsRequestFilter.class);

  private final KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();


  @Override
  public HttpResponse doFilter(HttpRequest originalRequest, HttpObject httpObject,
      ChannelHandlerContext channelHandlerContext) {
    if (httpObject instanceof FullHttpRequest) {
      FullHttpRequest fullHttpRequest = (FullHttpRequest) httpObject;
      String url = originalRequest.uri();
      int index = url.indexOf("?");
      if (index > -1) {
        url = url.substring(0, index);
      }
      CompositeByteBuf contentBuf = (CompositeByteBuf) fullHttpRequest.content();
      StatefulKnowledgeSession kSession = null;
      Map<String, Set<String>> rules = super.getUrlRule(DroolsRequestFilter.this);
      Set<String> urlRules = rules.get(url);
      if (urlRules != null && urlRules.size() == 1) {
        String droolsDrlRule = urlRules.iterator().next();
        try {
          kb.add(ResourceFactory.newByteArrayResource(droolsDrlRule.getBytes("utf-8")),
              ResourceType.DRL);
          KnowledgeBuilderErrors errors = kb.getErrors();
          String errorstr = null;
          for (KnowledgeBuilderError error : errors) {
            errorstr = errorstr + error.getMessage() + "\n";
          }
          if (errorstr != null) {
            throw new java.lang.IllegalArgumentException(errorstr);
          }
          KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
          kBase.addKnowledgePackages(kb.getKnowledgePackages());
          ForWardAction forwardAction = new ForWardAction();
          kSession = kBase.newStatefulKnowledgeSession();
          kSession.insert(new HeaderMapping(fullHttpRequest));
          kSession.insert(new BodyMapping(contentBuf));
          kSession.insert(forwardAction);
          kSession.fireAllRules();
          String targetUrl = forwardAction.getTargetUrl();
          if (targetUrl != null) {
            String requestUrl = ProxyUtils.parseUrl(targetUrl);
            fullHttpRequest.setUri(requestUrl);
            String hostAndPort = ProxyUtils.parseHostAndPort(targetUrl);
            if (!StringUtils.isBlank(hostAndPort)) {
              fullHttpRequest.headers().set(HttpHeaderNames.HOST, hostAndPort);
            }
          }
        } catch (Throwable e) {
          LOG.error(e.getMessage(), e);
          super.writeFilterLog(droolsDrlRule, this.getClass(), "droolsDrlRule");
          return super.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, originalRequest,
              "Drools Rule Error");
        } finally {
          if (kSession != null)
            kSession.dispose();
        }
      }
    }
    return null;
  }


  @Override
  public RequestFilterTypeEnum filterType() {
    return RequestFilterTypeEnum.DroolsRequestFilter;
  }


  public static class ForWardAction {
    private String targetUrl;

    public String getTargetUrl() {
      return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
      this.targetUrl = targetUrl;
    }
  }


  public static <T> T callRemoteService(String serviceId, String path, Object request,
      String httpMethod, Class<T> responseType) {
    DynamicSpringCloudClient springCloudClient =
        SpringContextHolder.getBean(DynamicSpringCloudClient.class);
    String response = springCloudClient.doHttpRemoteCall(serviceId, path, httpMethod, request);
    return JSON.parseObject(response, responseType);
  }

}
