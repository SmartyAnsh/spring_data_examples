package com.smartdiscover.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = {"com.smartdiscover.repository"})
public class CassandraConfig {

    @Bean
    public CqlSession session() {
        return CqlSession.builder().withKeyspace("educative").build();
    }

}