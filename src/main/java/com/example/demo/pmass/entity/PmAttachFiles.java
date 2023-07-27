package com.example.demo.pmass.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class PmAttachFiles implements Serializable {

    @Id
    private String attachId;

    private String  busId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileBody;

    private String fileName;

}
