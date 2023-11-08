package com.example.demo.bzHelp.entity.history;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description  
 * @Author  LongY
 * @Date 2023-09-21 
 */

@Data
@Entity
public class DevHolderBaseInfo implements Serializable {


	/**
	 * 股东id
	 */
	@Id
	private String holderCode;

	/**
	 * 股东姓名
	 */
	private String holderName;

	/**
	 * 客户编号
	 */
	private String custCode;

	/**
	 * 持股比例
	 */
	private String holderStockRate;

	/**
	 * 业务范围(公司)
	 */
	private String holderBusinessScope;

	/**
	 * 行业经验(年)
	 */
	private String holderBusinessSuffer;

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
