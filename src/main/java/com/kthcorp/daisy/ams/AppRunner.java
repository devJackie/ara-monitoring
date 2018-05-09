package com.kthcorp.daisy.ams;

import com.kthcorp.daisy.ams.executor.CommonExecutor;
import com.kthcorp.daisy.ams.service.AmsUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AppRunner implements ApplicationRunner {

    @Autowired
    private AmsUserService amsUserService;

//    @Autowired
//    private CommonService commonService;

    private final ApplicationContext context;

    @Autowired
    public AppRunner(ApplicationContext context) {
        this.context = context;
    }

//    @Override
//    public void run(String... args) {
//
//        try {
//            // Start the clock
//            long start = System.currentTimeMillis();
//
//            // Kick of multiple, asynchronous lookups
////        CompletableFuture<List<AmsUser>> page1 = amsUserService.findByUserName("test");
//
//            // business logic start
////            CompletableFuture<String> results = commonService.executeTask("test");
//            Executor executor = context.getBean(Executor.class);
//            CompletableFuture<List<AmsUser>> results = commonService.executeTask1("test");
//
//            // Wait until they are all done
////        CompletableFuture.allOf(phase1,phase2,phase3).join();
//
//            // Print results, including elapsed time
//            log.info("Elapsed time: " + (System.currentTimeMillis() - start));
////        log.info("--> {}", page1.get());
//        } catch (Exception e) {
//            log.error("", e);
//        } finally {
//
//        }
//    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // Start the clock
            long start = System.currentTimeMillis();

            // Kick of multiple, asynchronous lookups
//        CompletableFuture<List<AmsUser>> page1 = amsUserService.findByUserName("test");

            // business logic start
//            CompletableFuture<String> results = commonService.executeTask("test");
            Yaml yaml = new Yaml();
            Map config = (Map) yaml.load(new ClassPathResource("amoeba-collector.yml").getInputStream());
            CommonExecutor executor = context.getBean(CommonExecutor.class, config);
            CompletableFuture<String> phase = executor.executeTask();
//            CompletableFuture<List<AmsUser>> results = commonService.executeTask1("test");

            // Wait until they are all done
//        CompletableFuture.allOf(phase1,phase2,phase3).join();

            // Print results, including elapsed time
            log.info("Elapsed time: " + (System.currentTimeMillis() - start));
        log.info("--> {}", phase.get());
        } catch (Exception e) {
            log.error("", e);
        } finally {

        }
    }
}
