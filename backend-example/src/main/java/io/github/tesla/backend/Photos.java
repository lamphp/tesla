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
package io.github.tesla.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;


/**
 * @author liushiming
 * @version Photos.java, v 0.0.1 2018年4月24日 下午12:59:57 liushiming
 */
public class Photos extends ArrayList<Photo> {

  private static final long serialVersionUID = 1L;

  public Photos() {
    super();
  }

  public static void main(String[] args) throws IOException {
    InputStream in = Photos.class.getClassLoader().getResourceAsStream("Photos.json");
    String json = IOUtils.toString(in);
    List s = new Gson().fromJson(json, List.class);
    System.out.println(s);
//    Photo p1 = new Photo();
//    p1.setId("123");
//    p1.setIsfamily(1);
//    p1.setIsfriend(1);
//    p1.setIspublic(1);
//    p1.setOwner("liushiming");
//    p1.setTitle("shim");
//
//    Photo p = new Photo();
//    p.setId("123");
//    p.setIsfamily(1);
//    p.setIsfriend(1);
//    p.setIspublic(1);
//    p.setOwner("liushiming");
//    p.setTitle("shim");
//    
//    Photos photos = new Photos();
//    photos.add(p1);
//    photos.add(p);
//    System.out.println(new Gson().toJson(photos));
    

  }

}
