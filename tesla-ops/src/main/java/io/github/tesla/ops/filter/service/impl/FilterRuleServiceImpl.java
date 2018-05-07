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
package io.github.tesla.ops.filter.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tesla.common.dao.FilterDao;
import io.github.tesla.common.domain.FilterDO;
import io.github.tesla.ops.filter.service.FilterRuleService;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * @author liushiming
 * @version FilterRuleServiceImpl.java, v 0.0.1 2018年3月20日 上午11:02:55 liushiming
 */
@Service
public class FilterRuleServiceImpl implements FilterRuleService {

  @Autowired
  private FilterDao ruleDao;

  @Override
  public PageDO<FilterDO> queryList(Query query) {
    int total = ruleDao.count(query);
    List<FilterDO> rules = ruleDao.list(query);
    PageDO<FilterDO> page = new PageDO<>();
    page.setTotal(total);
    page.setRows(rules);
    return page;
  }

  @Override
  public FilterDO get(Long ruleId) {
    FilterDO rule = ruleDao.get(ruleId);
    return rule;
  }

  @Override
  public List<FilterDO> list(Map<String, Object> map) {
    List<FilterDO> routes = ruleDao.list(map);
    return routes;
  }

  @Override
  public int count(Map<String, Object> map) {
    return ruleDao.count(map);
  }

  @Override
  public int save(FilterDO ruleDo) {
    return ruleDao.save(ruleDo);
  }

  @Override
  public int update(FilterDO ruleDo) {
    return ruleDao.update(ruleDo);
  }

  @Override
  public int remove(Long ruleId) {
    return ruleDao.remove(ruleId);
  }

  @Override
  public int batchRemove(Long[] ruleIds) {
    return ruleDao.batchRemove(ruleIds);
  }

}
