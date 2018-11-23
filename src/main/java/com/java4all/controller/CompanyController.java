package com.java4all.controller;

import com.java4all.annotation.*;
import com.java4all.annotation.ParameterCacheable;
import com.java4all.entity.Company;
import com.java4all.service.CompanyService;
import com.java4all.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * 测试自定义注解 和 aop
 * @author wangzx
 * @version v1.0
 * @date 2018/11/22 9:37
 */
@RestController
@RequestMapping(value = "company")
public class CompanyController {

  @Autowired
  private CompanyService companyService;

  @GetMapping(value = "getCompany")
  @ResponseBody
  @RedisCacheable(cacheName = "id",value = "#id")
  public String getCompany(String id,@ParameterCacheable(key = "companyName") String companyName)throws Exception{

    Company company = companyService.getById(id);
    String companyStr = JsonUtil.toJSONString(company, true);
    System.out.println("======>【方法本体在执行】:"+companyStr);
    return companyStr;
  }
}
