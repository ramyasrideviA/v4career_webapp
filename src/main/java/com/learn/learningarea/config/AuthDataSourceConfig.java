package com.learn.learningarea.config;

import jakarta.persistence.EntityManagerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.learn.learningarea.repository.auth", 
    entityManagerFactoryRef = "authEntityManager", 
    transactionManagerRef = "authTransactionManager"
)
public class AuthDataSourceConfig {

    @Bean
    @Qualifier("authDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.auth")
    public DataSource authDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "authEntityManager")
    public LocalContainerEntityManagerFactoryBean authEntityManager(
            @Qualifier("authDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.learn.learningarea.model");
        em.setPersistenceUnitName("auth");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean(name = "authTransactionManager")
    public PlatformTransactionManager authTransactionManager(
            @Qualifier("authEntityManager") EntityManagerFactory authEntityManager) {
        return new JpaTransactionManager(authEntityManager);
    }
}
