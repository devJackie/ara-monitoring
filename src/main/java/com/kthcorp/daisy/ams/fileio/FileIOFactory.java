package com.kthcorp.daisy.ams.fileio;

import com.kthcorp.daisy.ams.properties.AmsMetaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 9..
 */
@Slf4j
@Configuration
public class FileIOFactory {


    private AmsMetaProperties amsMetaProperties;

    @Autowired
    FileIOFactory(AmsMetaProperties amsMetaProperties){
        this.amsMetaProperties = amsMetaProperties;
    }

    @Bean
    @Lazy
    @Scope("prototype")
    public FileIO fileIO(Map<String, Object> config) throws Exception {
        log.debug("config -> {}", config);
        String indexType = (String) config.get("type");
        if ("IDX".equalsIgnoreCase(indexType)) {
            log.info("Create IdxFileIO");
            return new IdxFileIO(config, amsMetaProperties);
        }
        throw new IllegalArgumentException("type: " + indexType);
    }
}
