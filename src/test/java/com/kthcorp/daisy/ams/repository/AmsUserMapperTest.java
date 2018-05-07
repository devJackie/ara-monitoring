package com.kthcorp.daisy.ams.repository;

import com.kthcorp.daisy.ams.repository.entity.AmsUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Transactional
@Rollback
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AmsUserMapperTest {

    @Autowired
    private AmsUserMapper amsUserMapper;

    @Test
    public void findByUserName() throws Exception {
        List<AmsUser> users = amsUserMapper.findByUserName("test");
        users.forEach(user -> {
            log.info("{}", user);
            assertThat(user.getUserName()).isNotNull();
        });
    }

}