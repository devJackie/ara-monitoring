package com.kthcorp.daisy.ams;

import com.kthcorp.daisy.ams.executor.CommonExecutor;
import com.kthcorp.daisy.ams.properties.AmsMetaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AppRunner implements ApplicationRunner {

    private final ApplicationContext context;
    private final static String MUTEX = "mutex";
    private final static String LINK_SERVER = "linkServer";
    private final ZkClient zkClient;
    private final AmsMetaProperties amsMetaProperties;

    String executeGroup;

    @Autowired
    public AppRunner(ApplicationContext context, ZkClient zkClient, AmsMetaProperties amsMetaProperties) {
        this.context = context;
        this.zkClient = zkClient;
        this.amsMetaProperties = amsMetaProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            // Start the clock
            long start = System.currentTimeMillis();

            // business logic start
            if (args.getOptionValues("executeGroup") != null && args.getOptionValues("executeGroup").size() > 0) {
                executeGroup = args.getOptionValues("executeGroup").get(0);
            } else {
                executeGroup = (String) amsMetaProperties.getAmsMeta().get("execute").get("default-execute-group");
            }
            log.info("executeGroup: {}", executeGroup);

            Yaml yaml = new Yaml();
            Map rootConfig = (Map) yaml.load(new ClassPathResource("ams-collector.yml").getInputStream());
            Map<String, Object> executeGroupConfig = (Map<String, Object>) rootConfig.get(executeGroup);

            Map mutex = (Map) executeGroupConfig.get(MUTEX);
            if (mutex != null) {
                String groupMutexUri = (String) mutex.get("uri");
                if (!zkClient.acquire(groupMutexUri)) {
                    log.warn("Cannot acquire mutex");
                    System.exit(0);
                }
            }

            ((List<Map<String, String>>) executeGroupConfig.get(LINK_SERVER)).forEach(x -> x.forEach(this::executeProcess));

            if (mutex != null) {
                String groupMutexUri = (String) mutex.get("uri");
                zkClient.acquireRelease(groupMutexUri);
            }

            // Print results, including elapsed time
            log.info("Elapsed time: " + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            log.error("", e);
        } finally {

        }
    }

    private void executeProcess(String profileName, String ymlPath) {
        try {
            Yaml yaml = new Yaml();
            log.debug("{}", profileName);
            log.debug("{}", ymlPath);
            Map config = (Map) yaml.load(new ClassPathResource(ymlPath).getInputStream());
            config.put("executeGroup", executeGroup);
            config.put("executeName", profileName);
            config.put("profileName", profileName);
            log.debug("config : {}", config);
            CommonExecutor executor = context.getBean(CommonExecutor.class, config);
            CompletableFuture<String> phase = executor.executeTask();

            // Wait until they are all done
            CompletableFuture.allOf(phase).join();
            log.debug("--> {}", phase.get());
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
