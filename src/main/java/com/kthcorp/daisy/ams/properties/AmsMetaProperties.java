package com.kthcorp.daisy.ams.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by devjackie on 2018. 5. 10..
 */
@Data
@ToString
@EqualsAndHashCode
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class AmsMetaProperties {

    private Map<String, Map<String, Object>> amsMeta = new HashMap<>();
}
