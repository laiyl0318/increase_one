package com.jiayi.dao.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 数据源配置
 * @author laiyilong
 */
@MapperScan(basePackages = "com.jiayi.dao.mapper")
@Configuration
@EnableTransactionManagement
@Slf4j
public class DataSourceConfig {
    @Value("${spring.datasource.type}")
    private Class<? extends DataSource> dataSourceType;

    @Bean(name = "primaryDB")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    @Primary
    public DataSource primaryDataSource() {
        log.info("-------------------- primaryDb init ---------------------");
        return DataSourceBuilder.create().type(dataSourceType).build();
    }

    /**
     * 事务管理器的datasource 需要与 sqlSessionFactory的datasource一致，
     *
     * @param primaryDB
     * @return
     */
    @Bean(name = "transactionManager")
    @Primary
    @Order(2)
    public DataSourceTransactionManager primaryTransactionManager(@Qualifier("primaryDB") DataSource primaryDB) {
        log.info("-------------------- transactionManager init ---------------------");
        return new DataSourceTransactionManager(primaryDB);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("primaryDB") DataSource primaryDB) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(primaryDB);
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
        sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactory;
    }

}
