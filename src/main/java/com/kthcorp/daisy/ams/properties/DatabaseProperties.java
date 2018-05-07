package com.kthcorp.daisy.ams.properties;

/**
 * Created by devjackie on 2018. 5. 3..
 */
public interface DatabaseProperties {

    public String getDriverClassName();

    public String getUrl();

    public String getUserName();

    public String getPassword();

    public boolean isInitialize();

    public int getInitialSize();

    public int getMaxActive();

    public int getMaxIdle();

    public int getMinIdle();

    public int getMaxWait();
}