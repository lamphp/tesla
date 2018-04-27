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

import java.io.Serializable;

/**
 * @author liushiming
 * @version Photo.java, v 0.0.1 2018年4月24日 下午12:01:40 liushiming
 */
public class Photo implements Serializable {
  private static final long serialVersionUID = 6169704107816633911L;
  private String id;
  private String owner;
  private String title;
  private int ispublic;
  private int isfriend;
  private int isfamily;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getIspublic() {
    return ispublic;
  }

  public void setIspublic(int ispublic) {
    this.ispublic = ispublic;
  }

  public int getIsfriend() {
    return isfriend;
  }

  public void setIsfriend(int isfriend) {
    this.isfriend = isfriend;
  }

  public int getIsfamily() {
    return isfamily;
  }

  public void setIsfamily(int isfamily) {
    this.isfamily = isfamily;
  }

  public static void main(String args) {

  }


}
