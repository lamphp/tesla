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
package io.github.tesla.gateway.netty.filter.help;

import io.github.tesla.gateway.config.SpringContextHolder;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;

/**
 * @author liushiming
 * @version RuleContent.java, v 0.0.1 2018年5月9日 下午8:45:37 liushiming
 */
public class DroolsContent {

  private String targetUrl;

  private String response;

  public String getTargetUrl() {
    return targetUrl;
  }

  public void setTargetUrl(String targetUrl) {
    this.targetUrl = targetUrl;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public String callRemoteService(String serviceId, String path, String submitJSON,
      String httpMethod) {
    DynamicSpringCloudClient springCloudClient =
        SpringContextHolder.getBean(DynamicSpringCloudClient.class);
    return springCloudClient.doHttpRemoteCall(serviceId, path, httpMethod, submitJSON);
  }

}
