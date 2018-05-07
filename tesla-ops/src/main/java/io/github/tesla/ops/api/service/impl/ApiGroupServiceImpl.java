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
package io.github.tesla.ops.api.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tesla.common.dao.ApiGroupDao;
import io.github.tesla.common.domain.ApiGroupDO;
import io.github.tesla.ops.api.service.ApiGroupService;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * @author liushiming
 * @version APIGroupServiceImpl.java, v 0.0.1 2018年4月11日 下午4:06:23 liushiming
 */
@Service
public class ApiGroupServiceImpl implements ApiGroupService {

  @Autowired
  private ApiGroupDao apiGroupDao;

  @Override
  public PageDO<ApiGroupDO> queryList(Query query) {
    int total = apiGroupDao.count(query);
    List<ApiGroupDO> groupDOs = apiGroupDao.list(query);
    PageDO<ApiGroupDO> page = new PageDO<>();
    page.setTotal(total);
    page.setRows(groupDOs);
    return page;
  }

  @Override
  public ApiGroupDO get(Long id) {
    return apiGroupDao.get(id);
  }

  @Override
  public List<ApiGroupDO> list(Map<String, Object> map) {
    return apiGroupDao.list(map);
  }

  @Override
  public int count(Map<String, Object> map) {
    return apiGroupDao.count(map);
  }

  @Override
  public int save(ApiGroupDO apiGroupDO) {
    return apiGroupDao.save(apiGroupDO);
  }

  @Override
  public int update(ApiGroupDO apiGroupDO) {
    return apiGroupDao.update(apiGroupDO);
  }

  @Override
  public int remove(Long id) {
    return apiGroupDao.remove(id);
  }

  @Override
  public int batchRemove(Long[] ids) {
    return apiGroupDao.batchRemove(ids);
  }



}
