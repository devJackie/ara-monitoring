package com.kthcorp.daisy.ams.repository.entity;

import lombok.Data;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Data
public class AmsUser {

    private Long id;

    private String userName;

    protected AmsUser() {}
}
