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
import io.github.tesla.gateway.netty.filter.request.BlackCookieHttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.BlackIpHttpRequesFilter;
import io.github.tesla.gateway.netty.filter.request.BlackURLHttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.BlackUaHttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.DataMappingRequestFilter;
import io.github.tesla.gateway.netty.filter.request.DroolsRequestFilter;
import io.github.tesla.gateway.netty.filter.request.DubboTransformHttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.GrpcTransformHttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.HttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.RateLimitHttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.SecurityScannerHttpRequestFilter;
import io.github.tesla.gateway.netty.filter.request.URLParamHttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class HttpRequestFilterChain {
  public static final Map<String, HttpRequestFilter> filters = Maps.newTreeMap();
  private static final HttpRequestFilterChain filterChain = new HttpRequestFilterChain();

  static {
    HttpRequestFilter blackIpHttpRequesFilter = BlackIpHttpRequesFilter.newFilter();
    filters.put(blackIpHttpRequesFilter.filterName(), blackIpHttpRequesFilter);
    HttpRequestFilter blackCookieHttpRequestFilter = BlackCookieHttpRequestFilter.newFilter();
    filters.put(blackCookieHttpRequestFilter.filterName(), blackCookieHttpRequestFilter);
    HttpRequestFilter rateLimitHttpRequestFilter = RateLimitHttpRequestFilter.newFilter();
    filters.put(rateLimitHttpRequestFilter.filterName(), rateLimitHttpRequestFilter);
    HttpRequestFilter securityScannerHttpRequestFilter =
        SecurityScannerHttpRequestFilter.newFilter();
    filters.put(securityScannerHttpRequestFilter.filterName(), securityScannerHttpRequestFilter);
    HttpRequestFilter blackUaHttpRequestFilter = BlackUaHttpRequestFilter.newFilter();
    filters.put(blackUaHttpRequestFilter.filterName(), blackUaHttpRequestFilter);
    HttpRequestFilter blackURLHttpRequestFilter = BlackURLHttpRequestFilter.newFilter();
    filters.put(blackURLHttpRequestFilter.filterName(), blackURLHttpRequestFilter);
    HttpRequestFilter uRLParamHttpRequestFilter = URLParamHttpRequestFilter.newFilter();
    filters.put(uRLParamHttpRequestFilter.filterName(), uRLParamHttpRequestFilter);
    HttpRequestFilter droolsRequestFilter = DroolsRequestFilter.newFilter();
    filters.put(droolsRequestFilter.filterName(), droolsRequestFilter);
    HttpRequestFilter dataMappingFilter = DataMappingRequestFilter.newFilter();
    filters.put(dataMappingFilter.filterName(), dataMappingFilter);
    HttpRequestFilter dubboAdapterHttpRequestFilter = DubboTransformHttpRequestFilter.newFilter();
    filters.put(dubboAdapterHttpRequestFilter.filterName(), dubboAdapterHttpRequestFilter);
    HttpRequestFilter grpcAdapterHttpRequestFilter = GrpcTransformHttpRequestFilter.newFilter();
    filters.put(grpcAdapterHttpRequestFilter.filterName(), grpcAdapterHttpRequestFilter);
  }

  public static HttpRequestFilterChain requestFilterChain() {
    GroovyFilterCacheComponent filterComponent =
        SpringContextHolder.getBean(GroovyFilterCacheComponent.class);
    Map<String, String> groovyFilters = filterComponent.getRequestGroovyCode();
    for (Map.Entry<String, String> entry : groovyFilters.entrySet()) {
      String name = entry.getKey();
      String filter = entry.getValue();
      Class<?> clazz = GroovyCompiler.compile(filter);
      try {
        HttpRequestFilter requestFilter = (HttpRequestFilter) clazz.newInstance();
        filters.put(name, requestFilter);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    List<String> keys = filterComponent.getRequestDeleteKey();
    for (String key : keys) {
      filters.remove(key);
    }
    return filterChain;
  }


  public HttpResponse doFilter(HttpRequest originalRequest, HttpObject httpObject,
      ChannelHandlerContext channelHandlerContext) {
    List<Map.Entry<String, HttpRequestFilter>> list = Lists.newArrayList(filters.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, HttpRequestFilter>>() {
      public int compare(Entry<String, HttpRequestFilter> o1, Entry<String, HttpRequestFilter> o2) {
        return o1.getValue().filterType().order() - o2.getValue().filterType().order();
      }

    });
    for (Iterator<Map.Entry<String, HttpRequestFilter>> it = list.iterator(); it.hasNext();) {
      HttpRequestFilter filter = it.next().getValue();
      HttpResponse response = filter.doFilter(originalRequest, httpObject, channelHandlerContext);
      // 如果一个filter有返回值，将会中断下一个filter，这里需要注意filter的顺序，默认grpc->dubbo
      if (response != null) {
        return response;
      }
    }
    return null;
  }
}
