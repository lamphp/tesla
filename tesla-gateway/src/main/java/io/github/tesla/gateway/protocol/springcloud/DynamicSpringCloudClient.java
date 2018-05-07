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
package io.github.tesla.gateway.protocol.springcloud;

import java.net.URI;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import io.github.tesla.common.domain.ApiSpringCloudDO;

/**
 * @author liushiming
 * @version DynamicSpringCloudClient.java, v 0.0.1 2018年5月4日 上午11:53:15 liushiming
 */
public class DynamicSpringCloudClient {

  private static Logger LOG = LoggerFactory.getLogger(DynamicSpringCloudClient.class);

  private final OkHttpClient okHttpClient;

  private final EurekaClient eurekaClient;

  private final int httpPort;

  public DynamicSpringCloudClient(OkHttpClient okHttpClient, EurekaClient eurekaClient,
      int httpPort) {
    this.okHttpClient = okHttpClient;
    this.eurekaClient = eurekaClient;
    this.httpPort = httpPort;
  }

  public DynamicSpringCloudClient(OkHttpClient okHttpClient, int httpPort) {
    this.okHttpClient = okHttpClient;
    this.eurekaClient = null;
    this.httpPort = httpPort;
  }

  private InstanceInfo nextServer(String serviceId) {
    InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(serviceId, false);
    return instanceInfo;
  }


  public String loadBalanceCall(final ApiSpringCloudDO springCloudDo) {
    String serviceId = springCloudDo.getInstanceId();
    InstanceInfo instaneinfo = this.nextServer(serviceId);
    return instaneinfo.getHostName() + ":" + instaneinfo.getPort();
  }

  public String doHttpRemoteCall(String submitServiceId, String submitUrl, String submitType,
      Object submitObj) {
    final String httpUrl;
    if (submitServiceId != null) {
      InstanceInfo serviceInstance = this.nextServer(submitServiceId);
      httpUrl = buildUrl(submitUrl, serviceInstance.getHostName(), serviceInstance.getPort());
    } else {
      httpUrl = buildUrl(submitUrl, "127.0.0.1", httpPort);
    }
    final Response response;
    final Request request;
    try {
      if ("POST".equalsIgnoreCase(submitType) && submitObj != null) {
        MediaType medialType = MediaType.parse("application/json; charset=utf-8");
        String httpJson = JSON.toJSONString(submitObj);
        RequestBody requestBody = RequestBody.create(medialType, httpJson);
        request = new Request.Builder()//
            .url(httpUrl)//
            .post(requestBody)//
            .build();
        response = okHttpClient.newCall(request).execute();
      } else {
        request = new Request.Builder()//
            .url(httpUrl)//
            .get()//
            .build();
        response = okHttpClient.newCall(request).execute();
      }
      return response.isSuccessful() ? response.body().string() : null;
    } catch (Throwable e) {
      LOG.error("call Remote service error,url is:" + httpUrl + ",body is:" + submitObj, e);
    }
    return null;
  }

  private static Pattern HTTP_PREFIX = Pattern.compile("^https?://.*", Pattern.CASE_INSENSITIVE);

  private String buildUrl(String path, String httpHost, int port) {
    final String url;
    if (HTTP_PREFIX.matcher(path).matches()) {
      url = path;
    } else {
      if (!path.startsWith("/")) {
        path = "/" + path;
      }
      url = String.format("http://%s:%s%s", httpHost, port, path);
    }
    return url;
  }

  public static void main(String[] args) {
    URI uri = URI.create("http://www.baidu.com/test/test");
    System.out.println(uri.getHost() + ":" + uri.getPort());
  }
}
