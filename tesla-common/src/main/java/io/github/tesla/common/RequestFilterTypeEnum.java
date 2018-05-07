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
package io.github.tesla.common;

/**
 * @author liushiming
 * @version FilterOrder.java, v 0.0.1 2018年1月26日 下午4:07:44 liushiming
 */
public enum RequestFilterTypeEnum {


  /**
   * 各种限制
   */
  Oauth2HttpRequestFilter(0), // oauth2
  URLParamHttpRequestFilter(1), // URL参数黑名单参数拦截
  BlackCookieHttpRequestFilter(2), // Cookie黑名单拦截
  BlackUaHttpRequestFilter(3), // User-Agent黑名单拦截
  BlackURLHttpRequestFilter(4), // URL路径黑名单拦截
  BlackIpHttpRequesFilter(5), // IP黑名单
  SecurityScannerHttpRequestFilter(6), // 扫描
  RateLimitHttpRequestFilter(7), // 限流
  DroolsRequestFilter(8), // 规则转换
  DataMappingRequestFilter(9), // 数据格式转化Mapping



  /**
   * 协议适配
   */
  GRPC(100), //
  SPRINGCLOUD(101), //
  DUBBO(102);

  private int filterOrder;

  RequestFilterTypeEnum(int filteOrder) {
    this.filterOrder = filteOrder;
  }

  public int order() {
    return filterOrder;
  }

  public static RequestFilterTypeEnum fromTypeName(String typeName) {
    for (RequestFilterTypeEnum type : RequestFilterTypeEnum.values()) {
      if (type.name().equals(typeName)) {
        return type;
      }
    }
    return null;
  }


}
