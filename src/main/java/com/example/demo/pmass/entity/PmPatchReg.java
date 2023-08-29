package com.example.demo.pmass.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Data
@Entity
public class PmPatchReg {

    /**
     * 补丁编号
     */
    @Id
    private String  patchCode;

    /**
     * 补丁描述
     */
    private String patchDisc;

    /**
     * 开发人员
     */
    private String patchDever;

    /**
     * 附件
     */
    private String patchId;
    @Lob
    @Transient
    private byte[] fileBody;

    /**
     * 附件名称
     */
    @Transient
    private String fileName;

    /**
     * 涉及文件
     */
    private String patchFiles;

    /**
     * 状态
     * 01-登记
     * 02-已发测试环境
     * 03-验证完毕
     * 04-已投产
     * 05-待发版
     * 06-作废
     */

    private String stat;
}
