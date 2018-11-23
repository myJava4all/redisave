package com.java4all.serviceImpl;

import com.java4all.entity.Company;
import com.java4all.service.CompanyService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * description:
 *
 * @author wangzx
 * @version v1.0
 * @date 2018/11/21 15:36
 */
@Service
public class CompanyServiceImpl implements CompanyService {


  /**模拟查询数据库*/
  @Override
  public Company getById(String id) {
    Company company = new Company();
    company.setId(id);
    company.setCompanyName("阿里巴巴集团");
    company.setAddress("陕西");
    company.setLastYearTax(new BigDecimal(213.26));
    return company;
  }
}
