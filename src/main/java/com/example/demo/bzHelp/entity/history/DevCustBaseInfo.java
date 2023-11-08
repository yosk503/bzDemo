package com.example.demo.bzHelp.entity.history;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author LongY
 * @Date 2023-09-21
 */

@Data
@Entity
public class DevCustBaseInfo implements Serializable {


    /**
     * 主键id，自动生成，规则后期定义
     */
    @Id
    private String custCode;

    /**
     * 客户名称
     */
    private String custName;

    /**
     * 社会统一信用代码
     */
    private String socialCreditCode;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系地址
     */
    private String contactAddr;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮件
     */
    private String contactEmail;

    /**
     * 编码
     */
    private String postCode;

    /**
     * 注册资本(万元)
     */
    private BigDecimal registeCapital;

    /**
     * 成立日期
     */
    private String registeDate;

    /**
     * 开业日期
     */
    private String workingDate;

    /**
     * 实际经营年限
     */
    private String manageYrear;

    /**
     * 员工人数(人)
     */
    private int employeeNum;

    /**
     * 主营业务
     */
    private String mainBusiness;

    /**
     * 备注
     */
    private String remark;

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

    private int la;
    private BigDecimal mm;

}
