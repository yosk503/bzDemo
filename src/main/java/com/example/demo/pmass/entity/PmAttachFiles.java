package com.example.demo.pmass.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class PmAttachFiles implements Serializable {

    /**
     * 附件id
     */
    @Id
    private String attachId;

    /**
     *busId
     */
    private String  busId;

    /**
     * 附件body，懒加载
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileBody;

    /**
     * 附件名称
     */
    private String fileName;

}
