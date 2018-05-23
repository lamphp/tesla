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

import com.bkjk.common.auth.sdk.jwt.JWT;
import com.bkjk.common.auth.sdk.jwt.algorithms.Algorithm;
import io.github.tesla.common.RequestFilterTypeEnum;
import io.github.tesla.gateway.netty.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

import static io.github.tesla.gateway.netty.filter.help.JWTFilterConstant.*;

public class JWTRequestFilter extends HttpRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JWTRequestFilter.class);
  private static String AUTHORIZATION_HEADER_VALUE_PREFIX = "Bearer "; // Authorization: Bearer
                                                                       // xxxxx
  private static String SECRET_KEY = "1FihRrMitxjiEVC1ICytWdthUyWytD+7";

  @Override
  public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject httpObject) {
    final HttpRequest nettyRequest = servletRequest.getNettyRequest();
    // Biz logic
    // 1: search cookie 3. refresh & set cookie
    Cookie[] cookies = servletRequest.getCookies();
    Map<String, Cookie> cookieMap = new HashMap<>();
    for (Cookie cookie : cookies) {
      cookieMap.put(cookie.getName(), cookie);
    }

    if (cookieMap.containsKey(KE_ID_COOKIE_NAME) && cookieMap.containsKey(UUS_ID_COOKIE_NAME)) {
      // set attribute for @see JWTSetCookieResponseFilter
      servletRequest.setAttribute(KE_ID_NAME, cookieMap.get(KE_ID_COOKIE_NAME));
      servletRequest.setAttribute(UUS_ID_NAME, cookieMap.get(UUS_ID_COOKIE_NAME));
      return null;
    }

    // 2: find Authorization: Bearer
    String authorizationHeaderValue = servletRequest.getHeader("Authorization");
    if (authorizationHeaderValue != null) {
      LOGGER.info("Authorization: {}", authorizationHeaderValue);
      if (authorizationHeaderValue.startsWith(AUTHORIZATION_HEADER_VALUE_PREFIX)) {
        try {
          String token =
              authorizationHeaderValue.substring(AUTHORIZATION_HEADER_VALUE_PREFIX.length());
          // verify JWT token
          JWT.require(Algorithm.HMAC256(SECRET_KEY))//
              .build()//
              .verify(token);

          // set attribute for @see JWTSetCookieResponseFilter
          final String keId = JWT.decode(token)//
              .getClaim(KE_ID_NAME).asString();
          if (StringUtils.isEmpty(keId)) {
            throw new Exception("jwt token payload not contains ke_id.");
          }
          servletRequest.setAttribute(KE_ID_NAME, keId);

          final String uusId = JWT.decode(token)//
              .getClaim(UUS_ID_NAME).asString();
          if (StringUtils.isEmpty(uusId)) {
            throw new Exception("jwt token payload not contains uus_id.");
          }
          servletRequest.setAttribute(UUS_ID_NAME, uusId);

        } catch (Exception e) {
          super.writeFilterLog(authorizationHeaderValue, JWTRequestFilter.class, e.getMessage());
          return super.createResponse(HttpResponseStatus.FORBIDDEN, nettyRequest);
        }
      }
    }
    return null;
  }

  @Override
  public RequestFilterTypeEnum filterType() {
    return RequestFilterTypeEnum.JWTRequestFilter;
  }
}
