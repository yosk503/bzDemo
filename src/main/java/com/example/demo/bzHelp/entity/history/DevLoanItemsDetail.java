package com.example.demo.bzHelp.entity.history;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description  
 * @Author  LongY
 * @Date 2023-09-21 
 */

@Data
@Entity
public class  DevLoanItemsDetail   implements Serializable {


	/**
	 * 归档内码
	 */
   	@Id
	private String itemId;

	/**
	 * 主表id，做关联使用
	 */
	private String devLoanId;

	/**
	 * 租赁物名称
	 */
	private String itemNaem;

	/**
	 * 租赁物型号
	 */
	private String itemType;

	/**
	 * 租赁物制造商
	 */
	private String itemMadeBus;

	/**
	 * 租赁物数量
	 */
	private BigDecimal itemNum;

	/**
	 * 租赁物单位
	 */
	private String itemUnit;

	/**
	 * 租赁物转让价格
	 */
	private BigDecimal transferAmt;

	/**
	 * 创建人
	 */
	private String createOper;

	/**
	 * 创建时间(精确)
	 */
	private String createTime;

	/**
	 * 最后更新人
	 */
	private String lastUpdOper;

	/**
	 * 最后更新时间(精确)
	 */
	private String lastUpdTime;
}
