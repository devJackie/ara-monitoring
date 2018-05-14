package com.kthcorp.daisy.ams.fileio;

import com.kthcorp.daisy.ams.properties.AmsMetaProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015-08-25.
 */
@Data
@Slf4j
public abstract class BaseFileIO implements FileIO {

    protected Map<String, String> header  = new HashMap<>();

    protected final Map<String, Object> config;

    protected final String path;
    protected final String textEncoding;
    protected final List<Map<String, Object>> pathAttributes;

    protected AmsMetaProperties amsMetaProperties;

    protected BaseFileIO(Map<String, Object> config, AmsMetaProperties amsMetaProperties) {
        this.config = config;
        this.amsMetaProperties = amsMetaProperties;
        this.path = (String) config.get(amsMetaProperties.getAmsMeta().get("common").get("path"));
        this.pathAttributes = (List) config.get(amsMetaProperties.getAmsMeta().get("common").get("path-attribute"));
        this.textEncoding = (String) config.get(amsMetaProperties.getAmsMeta().get("common").get("text-encoding"));
    }
}
