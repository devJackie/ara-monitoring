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
public class AmsUser {

    private Long id;

    private String userName;

    protected AmsUser() {}
}
