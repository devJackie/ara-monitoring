package com.kthcorp.daisy.ams.repository.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Data
@ToString
@EqualsAndHashCode
public class RecFileInfo {

    private String yyyyMMdd;
    private String aplnFormId;
    private String adNo;
    private String chId;
    private String otvChNo;
    private String startDt;
    private String recThumbFilePath;
    private String recFilePath;
    private String regDt;

    public RecFileInfo() {}
}
