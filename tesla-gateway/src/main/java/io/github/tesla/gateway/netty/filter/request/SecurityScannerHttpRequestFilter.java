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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.tesla.common.RequestFilterTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 安全扫描限制
 */
public class SecurityScannerHttpRequestFilter extends HttpRequestFilter {

  @Override
  public HttpResponse doFilter(HttpRequest originalRequest, HttpObject httpObject,
      ChannelHandlerContext channelHandlerContext) {
    if (httpObject instanceof FullHttpRequest) {
      FullHttpRequest httpRequest = (FullHttpRequest) httpObject;
      boolean acunetixAspect = httpRequest.headers().contains("Acunetix-Aspect");
      boolean acunetixAspectPassword = httpRequest.headers().contains("Acunetix-Aspect-Password");
      boolean acunetixAspectQueries = httpRequest.headers().contains("Acunetix-Aspect-Queries");
      boolean xScanMemo = httpRequest.headers().contains("X-Scan-Memo");
      boolean xRequestMemo = httpRequest.headers().contains("X-Request-Memo");
      boolean xRequestManagerMemo = httpRequest.headers().contains("X-RequestManager-Memo");
      boolean xWIPP = httpRequest.headers().contains("X-WIPP");
      Pattern pattern1 = Pattern.compile("AppScan_fingerprint");
      Matcher matcher1 = pattern1.matcher(httpRequest.uri());
      String bsKey = "--%3E%27%22%3E%3CH1%3EXSS%40HERE%3C%2FH1%3E";
      boolean matcher2 = httpRequest.uri().contains(bsKey);
      Pattern pattern3 = Pattern.compile("netsparker=");
      Matcher matcher3 = pattern3.matcher(httpRequest.uri());
      if (acunetixAspect || acunetixAspectPassword || acunetixAspectQueries) {
        super.writeFilterLog(httpRequest.headers().toString(), this.getClass(),
            "Acunetix Web Vulnerability");
        return super.createResponse(HttpResponseStatus.FORBIDDEN, originalRequest);
      } else if (xScanMemo || xRequestMemo || xRequestManagerMemo || xWIPP) {
        super.writeFilterLog(httpRequest.headers().toString(), this.getClass(), "HP WebInspect");
        return super.createResponse(HttpResponseStatus.FORBIDDEN, originalRequest);
      } else if (matcher1.find()) {
        super.writeFilterLog(httpRequest.headers().toString(), this.getClass(), "Appscan");
        return super.createResponse(HttpResponseStatus.FORBIDDEN, originalRequest);
      } else if (matcher2) {
        super.writeFilterLog(httpRequest.headers().toString(), this.getClass(), "Bugscan");
        return super.createResponse(HttpResponseStatus.FORBIDDEN, originalRequest);
      } else if (matcher3.find()) {
        super.writeFilterLog(httpRequest.headers().toString(), this.getClass(), "Netsparker");
        return super.createResponse(HttpResponseStatus.FORBIDDEN, originalRequest);
      }
    }
    return null;
  }

  @Override
  public RequestFilterTypeEnum filterType() {
    return RequestFilterTypeEnum.SecurityScannerHttpRequestFilter;
  }

}
