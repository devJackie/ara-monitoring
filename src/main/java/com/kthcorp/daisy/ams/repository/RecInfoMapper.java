package com.kthcorp.daisy.ams.repository;


import com.kthcorp.daisy.ams.repository.entity.RecFileInfo;
import com.kthcorp.daisy.ams.repository.support.AmsSchema;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by devjackie on 2018. 5. 13..
 */
@AmsSchema
public interface RecInfoMapper {

    void insertRecFileInfo(RecFileInfo recFileInfo) throws Exception;
}
