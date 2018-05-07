package com.kthcorp.daisy.ams.service;

import com.kthcorp.daisy.ams.repository.AmsUserMapper;
import com.kthcorp.daisy.ams.repository.entity.AmsUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by devjackie on 2018. 5. 6..
 */
@Service
@Slf4j
public class AmsUserService {

    @Autowired
    private AmsUserMapper amsUserMapper;

    @Async
    public CompletableFuture<List<AmsUser>> findByUserName(String user) throws InterruptedException {
        log.info("Looking up {}", user);
        List<AmsUser> results = amsUserMapper.findByUserName("test");
        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(results);
    }
}
