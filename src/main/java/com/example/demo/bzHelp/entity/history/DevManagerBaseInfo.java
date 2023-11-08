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
public class DevManagerBaseInfo implements Serializable {


	/**
	 * 管理人员id
	 */
	@Id
	private String managerCode;

	/**
	 * 客户编号
	 */
	private String custCode;

	/**
	 * 姓名
	 */
	private String managerName;

	/**
	 * 下拉框形式，性别
	 */
	private String managerSex;

	/**
	 * 年龄
	 */
	private String managerAge;

	/**
	 * 下拉框形式，职务
	 */
	private String managerJob;

	/**
	 * 学历
	 */
	private String managerEdu;

	/**
	 * 业务范围(公司)
	 */
	private String managerBusinessScope;

	/**
	 * 行业经验(年)
	 */
	private String businessSuffer;

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
