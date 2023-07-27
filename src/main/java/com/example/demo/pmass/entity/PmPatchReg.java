package com.example.demo.pmass.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Data
@Entity
public class PmPatchReg {
    @Id
    private String  patchCode;

    private String patchDisc;

    private String patchDever;
    @Lob
    @Transient
    private byte[] fileBody;

    @Transient
    private String fileName;

    public String getPatchCode() {
        return patchCode;
    }

    public void setPatchCode(String patchCode) {
        this.patchCode = patchCode;
    }

    public String getPatchDisc() {
        return patchDisc;
    }

    public void setPatchDisc(String patchDisc) {
        this.patchDisc = patchDisc;
    }

    public String getPatchDever() {
        return patchDever;
    }

    public void setPatchDever(String patchDever) {
        this.patchDever = patchDever;
    }

    public  byte[] getFileBody() {
        return fileBody;
    }

    public void setFileBody( byte[] fileBody) {
        this.fileBody = fileBody;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
