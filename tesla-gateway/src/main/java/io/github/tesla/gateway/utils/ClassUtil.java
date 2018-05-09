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
package io.github.tesla.gateway.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.gateway.netty.filter.request.HttpRequestFilter;


/**
 * @author liushiming
 * @version PackageUtil.java, v 0.0.1 2018年5月9日 上午9:59:02 liushiming
 */
public final class ClassUtil {

  private ClassUtil() {}

  private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);

  private static final String FILE_TYPE = "file";

  private static final String JAR_TYPE = "jar";


  public static ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }


  public static Class<?> loadClass(String className, boolean isInitialized) {
    Class<?> cls = null;
    try {
      cls = Class.forName(className, isInitialized, getClassLoader());
    } catch (ClassNotFoundException e) {
      logger.error("load class failure", e);
      throw new RuntimeException();
    }
    return cls;
  }


  public static Set<Class<?>> getClassSet(String packageName) {
    Set<Class<?>> classSets = new HashSet<Class<?>>();
    try {
      Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        String protocol = url.getProtocol();
        if (FILE_TYPE.equals(protocol)) {
          addFileClass(classSets, url.toURI(), packageName);
        } else if (JAR_TYPE.equals(protocol)) {
          addJarClass(classSets, url.toURI(), packageName);
        }
      }
    } catch (Exception e) {
      logger.error("get class set failure", e);
      throw new RuntimeException();
    }
    return classSets;
  }

  @SuppressWarnings("resource")
  private static void addJarClass(Set<Class<?>> classSets, URI uri, String packageName)
      throws IOException {
    String jarPath = StringUtils.substringBetween(uri.getSchemeSpecificPart(), ":", "!");
    if (jarPath == null)
      return;
    Enumeration<JarEntry> iterator = new JarFile(jarPath).entries();
    while (iterator.hasMoreElements()) {
      JarEntry jarEntry = iterator.nextElement();
      if (!jarEntry.isDirectory()) {
        String name = jarEntry.getName();
        int lastDotClassIndex = name.lastIndexOf(".class");
        if (lastDotClassIndex != -1) {
          int lastSlashIndex = name.lastIndexOf("/");
          name = name.replace("/", ".");
          if (name.startsWith(packageName)) {
            if (packageName.length() == lastSlashIndex) {
              String className = name.substring(0, lastDotClassIndex);
              doAddClass(classSets, className);
            }
          }
        }
      }
    }
  }

  private static void addFileClass(Set<Class<?>> classSets, URI uri, String packageName) {
    File[] files = Paths.get(uri).toFile().listFiles(new FileFilter() {
      public boolean accept(File file) {
        return ((file.isFile() && file.getName().endsWith(".class")) || (file.isDirectory()));
      }
    });
    for (File file : files) {
      String fileName = file.getName();
      if (file.isFile()) {
        String className = fileName.substring(0, fileName.lastIndexOf("."));
        if (StringUtils.isNotEmpty(className)) {
          className = packageName + "." + className;
        }
        doAddClass(classSets, className);
      } else {
        String subPath = fileName;
        if (StringUtils.isNotEmpty(subPath)) {
          subPath = packageName + "/" + subPath;
        }

        String subPackage = packageName;
        if (StringUtils.isNotEmpty(subPackage)) {
          subPackage = packageName + "." + fileName;
        }
        addFileClass(classSets, Paths.get(subPath).toUri(), subPackage);
      }
    }
  }


  private static void doAddClass(Set<Class<?>> classSets, String className) {
    Class<?> cls = loadClass(className, false);
    classSets.add(cls);
  }

  private static final String REQUEST_FILTER_PACKAGENAME =
      "io.github.tesla.gateway.netty.filter.request";

  public static void main(String[] args) {
    Set<Class<?>> requestFilterClazzs = ClassUtil.getClassSet(REQUEST_FILTER_PACKAGENAME);
    for (Class<?> clazz : requestFilterClazzs) {
      System.out.println(clazz);
      if (clazz.isAssignableFrom(HttpRequestFilter.class)
          && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {
        try {
          HttpRequestFilter filter = (HttpRequestFilter) clazz.newInstance();
          System.out.println(filter);
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
    }
  }
}
