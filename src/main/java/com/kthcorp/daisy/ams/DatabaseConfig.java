package com.kthcorp.daisy.ams;

import com.kthcorp.daisy.ams.properties.AmsDatabaseProperties;
import com.kthcorp.daisy.ams.properties.DatabaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Slf4j
public abstract class DatabaseConfig {

    abstract void initialize(org.apache.tomcat.jdbc.pool.DataSource dataSource);

    protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource, DatabaseProperties databaseProperties) {
        log.info("databaseProperties.getDriverClassName() : {}", databaseProperties.getDriverClassName());
        log.info("databaseProperties.getUrl() : {}", databaseProperties.getUrl());
        log.info("databaseProperties.getUserName() : {}", databaseProperties.getUserName());
        dataSource.setDriverClassName(databaseProperties.getDriverClassName());
        dataSource.setUrl(databaseProperties.getUrl());
        dataSource.setUsername(databaseProperties.getUserName());
        dataSource.setPassword(databaseProperties.getPassword());
        dataSource.setMaxActive(databaseProperties.getMaxActive());
        dataSource.setMaxIdle(databaseProperties.getMaxIdle());
        dataSource.setMinIdle(databaseProperties.getMinIdle());
        dataSource.setMaxWait(databaseProperties.getMaxWait());
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        if(databaseProperties.isInitialize())
            initialize(dataSource);
    }
}

@Configuration
@EnableConfigurationProperties(AmsDatabaseProperties.class)
class AmsDatabaseConfig extends DatabaseConfig {

    @Autowired
    private AmsDatabaseProperties amsDatabaseProperties;

    @Override
    protected void initialize(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
//        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
////        Resource schema = pathResolver.getResource("classpath:script/ams-user-schema.sql");
//        Resource data = pathResolver.getResource("classpath:script/ams-user-data.sql");
////        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(schema, data);
//        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(data);
//        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }

    @Bean(name = "amsDataSource", destroyMethod = "close")
    @Primary
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource amsDataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        configureDataSource(amsDataSource, amsDatabaseProperties);
        return amsDataSource;
    }

    @Bean(name = "amsTransactionManager")
    @Primary
    public PlatformTransactionManager amsTransactionManager(@Qualifier("amsDataSource") DataSource amsDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(amsDataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        return transactionManager;
    }
}
