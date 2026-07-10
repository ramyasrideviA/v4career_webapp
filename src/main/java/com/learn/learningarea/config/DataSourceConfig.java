package com.learn.learningarea.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

        @Bean
        @Primary
        public DataSource routingDataSource(
                        @Value("${spring.datasource.chennai.jdbc-url}") String chennaiUrl,
                        @Value("${spring.datasource.chennai.username}") String chennaiUsername,
                        @Value("${spring.datasource.chennai.password}") String chennaiPassword,

                        @Value("${spring.datasource.madurai.jdbc-url}") String maduraiUrl,
                        @Value("${spring.datasource.madurai.username}") String maduraiUsername,
                        @Value("${spring.datasource.madurai.password}") String maduraiPassword) {

                DataSource chennaiDS = DataSourceBuilder.create()
                                .url(chennaiUrl)
                                .username(chennaiUsername)
                                .password(chennaiPassword)
                                .build();

                DataSource maduraiDS = DataSourceBuilder.create()
                                .url(maduraiUrl)
                                .username(maduraiUsername)
                                .password(maduraiPassword)
                                .build();

                Map<Object, Object> dataSources = new HashMap<>();

                dataSources.put("CHENNAI", chennaiDS);
                dataSources.put("MADURAI", maduraiDS);

                BranchRoutingDataSource routingDS = new BranchRoutingDataSource();

                routingDS.setTargetDataSources(dataSources);
                routingDS.setDefaultTargetDataSource(chennaiDS);

                return routingDS;
        }

        @Bean
        public CommandLineRunner initializeSchemas(EntityManagerFactory emf) {

                return args -> {

                        try {

                                BranchContext.setBranch("CHENNAI");
                                emf.createEntityManager().close();

                                BranchContext.setBranch("MADURAI");
                                emf.createEntityManager().close();

                        } finally {

                                BranchContext.clear();
                        }
                };
        }
}