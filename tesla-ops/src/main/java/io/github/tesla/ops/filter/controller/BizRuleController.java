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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.filter.vo.FilterRuleVo;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * @author liushiming
 * @version BizRuleController.java, v 0.0.1 2018年5月13日 上午10:39:11 liushiming
 */
@Controller
@RequestMapping("/filter/bizrule")
public class BizRuleController extends ShareRuleController {

  private final String prefix = "gateway/bizrule";

  @RequiresPermissions("filter:rule:rule")
  @GetMapping()
  public String rule() {
    return prefix + "/rule";
  }

  @RequiresPermissions("filter:rule:add")
  @GetMapping("/add")
  public String add() {
    return prefix + "/add";
  }

  @RequiresPermissions("filter:rule:edit")
  @GetMapping("/edit/{id}")
  public String edit(@PathVariable("id") Long id, Model model) {
    FilterRuleVo ruleVo = ruleService.get(id);
    model.addAttribute("rule", ruleVo);
    return prefix + "/edit";
  }

  @Log("查询规则")
  @RequiresPermissions("filter:rule:rule")
  @GetMapping("/list")
  @ResponseBody
  public PageDO<FilterRuleVo> list(@RequestParam Map<String, Object> params) {
    Query query = new Query(params);
    query.put("bizrule", true);
    return ruleService.queryList(query);
  }
}
