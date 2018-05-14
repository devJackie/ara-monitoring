package com.kthcorp.daisy.ams.config;

import com.kthcorp.daisy.ams.repository.support.AmsSchema;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by devjackie on 2018. 5. 3..
 */
@Data
public abstract class MyBatisConfig {
    public static final String BASE_PACKAGE_PREFIX = "com.kthcorp.daisy.ams.repository";

    public static final String ENTITY_PACKAGE_PREFIX = "com.kthcorp.daisy.ams.repository.entity";

    public static final String CONFIG_LOCATION_PATH = "classpath:mybatis/mybatis-config.xml";

    public static final String MAPPER_LOCATIONS_PATH = "classpath:mybatis/mapper/**/*.xml";

    protected void configureSqlSessionFactory(SqlSessionFactoryBean sessionFactoryBean, DataSource dataSource) throws IOException {
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setTypeAliasesPackage(ENTITY_PACKAGE_PREFIX);
        sessionFactoryBean.setConfigLocation(pathResolver.getResource(CONFIG_LOCATION_PATH));
        sessionFactoryBean.setMapperLocations(pathResolver.getResources(MAPPER_LOCATIONS_PATH));
    }
}

@Configuration
@MapperScan(basePackages = MyBatisConfig.BASE_PACKAGE_PREFIX, annotationClass = AmsSchema.class, sqlSessionFactoryRef = "amsSqlSessionFactory")
class AmsMybatisConfig extends MyBatisConfig {

    @Bean(name = "amsSqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("amsDataSource") DataSource amsDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        configureSqlSessionFactory(sessionFactoryBean, amsDataSource);
        return sessionFactoryBean.getObject();
    }
}