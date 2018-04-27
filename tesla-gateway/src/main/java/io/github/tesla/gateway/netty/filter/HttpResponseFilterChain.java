package io.github.tesla.gateway.netty.filter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.tesla.gateway.cache.GroovyFilterCacheComponent;
import io.github.tesla.gateway.config.SpringContextHolder;
import io.github.tesla.gateway.netty.filter.response.ClickjackHttpResponseFilter;
import io.github.tesla.gateway.netty.filter.response.DataMappingHttpResponseFilter;
import io.github.tesla.gateway.netty.filter.response.HttpResponseFilter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;


public class HttpResponseFilterChain {
  public static final Map<String, HttpResponseFilter> filters = Maps.newTreeMap();
  private static HttpResponseFilterChain filterChain = new HttpResponseFilterChain();

  static {
    HttpResponseFilter clickjackHttpResponseFilter = ClickjackHttpResponseFilter.newFilter();
    filters.put(clickjackHttpResponseFilter.filterType().name(), clickjackHttpResponseFilter);
    HttpResponseFilter dataMapingHttpResonseFilter = DataMappingHttpResponseFilter.newFilter();
    filters.put(dataMapingHttpResonseFilter.filterType().name(), dataMapingHttpResonseFilter);
  }

  public static HttpResponseFilterChain responseFilterChain() {
    GroovyFilterCacheComponent filterComponent =
        SpringContextHolder.getBean(GroovyFilterCacheComponent.class);
    Map<String, String> groovyFilters = filterComponent.getResponseGroovyCode();
    for (Map.Entry<String, String> entry : groovyFilters.entrySet()) {
      String name = entry.getKey();
      String filter = entry.getValue();
      Class<?> clazz = GroovyCompiler.compile(filter);
      try {
        HttpResponseFilter responseFilter = (HttpResponseFilter) clazz.newInstance();
        filters.put(name, responseFilter);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    List<String> keys = filterComponent.getResponseDeleteKey();
    for (String key : keys) {
      filters.remove(key);
    }
    return filterChain;
  }


  public void doFilter(HttpRequest originalRequest, HttpResponse httpResponse) {
    List<Map.Entry<String, HttpResponseFilter>> list = Lists.newArrayList(filters.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, HttpResponseFilter>>() {
      public int compare(Entry<String, HttpResponseFilter> o1,
          Entry<String, HttpResponseFilter> o2) {
        return o1.getValue().filterType().order() - o2.getValue().filterType().order();
      }

    });
    for (Iterator<Map.Entry<String, HttpResponseFilter>> it = list.iterator(); it.hasNext();) {
      HttpResponseFilter filter = it.next().getValue();
      filter.doFilter(originalRequest, httpResponse);
    }
  }
}
