package com.kthcorp.daisy.ams.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Data
@ToString
@EqualsAndHashCode
@ConfigurationProperties(prefix = AmsDatabaseProperties.PREFIX)
public class AmsDatabaseProperties implements DatabaseProperties {

    public static final String PREFIX = "datasource.ams";

    public static final boolean DEFAULT_INITIALIZE = false;

    private boolean initialize = DEFAULT_INITIALIZE;

    private String driverClassName;

    private String url;

    private String userName;

    private String password;

    private int initialSize;

    private int maxActive;

    private int maxIdle;

    private int minIdle;

    private int maxWait;
}