package com.kthcorp.daisy.ams;

import com.kthcorp.daisy.ams.repository.entity.AmsUser;
import com.kthcorp.daisy.ams.service.AmsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AppRunner implements CommandLineRunner {

    @Autowired
    private AmsUserService amsUserService;

    @Override
    public void run(String... args) throws Exception {
        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        CompletableFuture<List<AmsUser>> page1 = amsUserService.findByUserName("test");

        // Wait until they are all done
//        CompletableFuture.allOf(page1,page2,page3).join();

        // Print results, including elapsed time
        log.info("Elapsed time: " + (System.currentTimeMillis() - start));
        log.info("--> {}", page1.get());
//        log.info("Elapsed time: " + (System.currentTimeMillis() - start));

    }
}
