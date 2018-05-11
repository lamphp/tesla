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
package io.github.tesla.ops.filter.controller;

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.tesla.common.domain.FilterDO;
import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.filter.service.FilterRuleService;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * @author liushiming
 * @version FilterRuleController.java, v 0.0.1 2018年3月20日 下午1:47:26 liushiming
 */
@Controller
@RequestMapping("/filter/sharerule")
public class FilterRuleController extends BaseController {
  private final String prefix = "gateway/sharerule";

  @Autowired
  private FilterRuleService ruleService;

  @RequiresPermissions("filter:rule:rule")
  @GetMapping()
  public String rule() {
    return prefix + "/filter";
  }

  @RequiresPermissions("filter:rule:add")
  @GetMapping("/add")
  public String add() {
    return prefix + "/add";
  }


  @Log("查询规则")
  @RequiresPermissions("filter:rule:rule")
  @GetMapping("/list")
  @ResponseBody
  public PageDO<FilterDO> list(@RequestParam Map<String, Object> params) {
    Query query = new Query(params);
    query.put("sharerule", true);
    return ruleService.queryList(query);
  }


  @RequiresPermissions("filter:rule:edit")
  @GetMapping("/edit/{id}")
  public String edit(@PathVariable("id") Long id, Model model) {
    FilterDO ruleDo = ruleService.get(id);
    model.addAttribute("rule", ruleDo);
    return prefix + "/edit";
  }

  @Log("保存规则")
  @ResponseBody
  @PostMapping("/save")
  @RequiresPermissions("filter:rule:add")
  public CommonResponse save(FilterDO rule) {
    if (ruleService.save(rule) > 0) {
      return CommonResponse.ok();
    }
    return CommonResponse.error();
  }

  @Log("保存规则")
  @ResponseBody
  @RequestMapping("/update")
  @RequiresPermissions("filter:rule:edit")
  public CommonResponse update(FilterDO rule) {
    if (ruleService.update(rule) > 0) {
      return CommonResponse.ok();
    }
    return CommonResponse.error();
  }

  @Log("删除规则")
  @PostMapping("/remove")
  @ResponseBody
  @RequiresPermissions("filter:rule:remove")
  public CommonResponse remove(Long ruleId) {
    if (ruleService.remove(ruleId) > 0) {
      return CommonResponse.ok();
    }
    return CommonResponse.error();
  }

  @Log("批量删除规则")
  @PostMapping("/batchRemove")
  @ResponseBody
  @RequiresPermissions("filter:rule:batchRemove")
  public CommonResponse remove(@RequestParam("ids[]") Long[] ruleIds) {
    ruleService.batchRemove(ruleIds);
    return CommonResponse.ok();
  }
}
