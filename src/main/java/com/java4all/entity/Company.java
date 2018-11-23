package com.java4all.entity;

import java.math.BigDecimal;
import lombok.Data;

/**
 * description:
 *
 * @author wangzx
 * @version v1.0
 * @date 2018/11/21 15:03
 */
@Data
public class Company {

  private String id;

  private String companyName;

  private String address;

  private BigDecimal lastYearTax;

}
