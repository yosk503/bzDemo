package com.example.demo.pmass.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;




@Entity
public class pmPatchReg implements Serializable {
    @Id
    private String  patchCode;

    private String patchDisc;

    private String patchDever;
    @Transient
    private String fileBody;
    @Transient
    private String fileName;

    public String  getPatchCode() {
        return patchCode;
    }

    public void setPatchCode(String  patchCode) {
        this.patchCode = patchCode;
    }

    public String getPatchDisc() {
        return patchDisc;
    }

    public void setPatchDisc(String patchDisc) {
        this.patchDisc = patchDisc;
    }

    public String getFileBody() {
        return fileBody;
    }

    public void setFileBody(String fileBody) {
        this.fileBody = fileBody;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPatchDever() {
        return patchDever;
    }

    public void setPatchDever(String patchDever) {
        this.patchDever = patchDever;
    }
}
