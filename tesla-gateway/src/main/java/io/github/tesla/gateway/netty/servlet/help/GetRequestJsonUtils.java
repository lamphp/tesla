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
package io.github.tesla.gateway.netty.servlet.help;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liushiming
 * @version GetRequestJsonUtils.java, v 0.0.1 2018年5月10日 上午11:25:22 liushiming
 */
public class GetRequestJsonUtils {

  public static String getRequestJsonString(HttpServletRequest request) throws IOException {
    String submitMehtod = request.getMethod();
    // GET
    if (submitMehtod.equals("GET")) {
      return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22",
          "\"");
      // POST
    } else {
      return getRequestPostStr(request);
    }
  }


  public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
    int contentLength = request.getContentLength();
    if (contentLength < 0) {
      return null;
    }
    byte buffer[] = new byte[contentLength];
    for (int i = 0; i < contentLength;) {

      int readlen = request.getInputStream().read(buffer, i, contentLength - i);
      if (readlen == -1) {
        break;
      }
      i += readlen;
    }
    return buffer;
  }


  public static String getRequestPostStr(HttpServletRequest request) throws IOException {
    byte buffer[] = getRequestPostBytes(request);
    String charEncoding = request.getCharacterEncoding();
    if (charEncoding == null) {
      charEncoding = "UTF-8";
    }
    return new String(buffer, charEncoding);
  }

}
