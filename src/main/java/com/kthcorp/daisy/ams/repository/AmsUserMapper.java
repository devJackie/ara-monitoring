package com.kthcorp.daisy.ams.repository;


import com.kthcorp.daisy.ams.repository.entity.AmsUser;
import com.kthcorp.daisy.ams.repository.support.AmsSchema;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@AmsSchema
public interface AmsUserMapper {

    public List<AmsUser> findAll();

    public List<AmsUser> findByUserName(@Param("userName") String userName);

    public AmsUser findOne(Long id);

    public Boolean exists(Long id);

    public void save(AmsUser users);

    public void update(AmsUser users);

    public void delete(AmsUser users);
}
