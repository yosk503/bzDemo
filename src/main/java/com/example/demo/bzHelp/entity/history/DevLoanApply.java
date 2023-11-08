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
public class  DevLoanApply   implements Serializable {


	/**
	 * 详细生成见规则
	 */
   	@Id
	private String applyId;

	/**
	 * 客户编号
	 */
	private String custCode;

	/**
	 * 合同编号
	 */
	private String contCode;

	/**
	 * 状态00-初始01-待确认02-已确认03-退回04-作废
	 */
	private String loanState;

	/**
	 * 转让物价格
	 */
	private BigDecimal transferTotalAmt;

	/**
	 * 初始租金
	 */
	private BigDecimal initAmt;

	/**
	 * 租赁价款
	 */
	private BigDecimal loanAmt;

	/**
	 * 执行利率
	 */
	private BigDecimal executeRate;

	/**
	 * 下拉框，还租方式，系统的字典，外加自定义
	 */
	private String loanRepayMethod;

	/**
	 * 保证金比例
	 */
	private BigDecimal marginRate;

	/**
	 * 合同签订日
	 */
	private String contSignDate;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 退回原因
	 */
	private String backReason;

	/**
	 * 登记人
	 */
	private String registOper;

	/**
	 * 登记时间(精确)
	 */
	private String registTime;

	/**
	 * 确认人
	 */
	private String confirmOper;

	/**
	 * 确认时间(精确)
	 */
	private String confirmTime;

	/**
	 * 最后更新人
	 */
	private String lastUpdOper;

	/**
	 * 最后更新时间
	 */
	private String lastUpdTime;
}
