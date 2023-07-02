package com.smartdiscover.config;

import com.couchbase.client.java.Cluster;
import com.couchbase.transactions.TransactionDurabilityLevel;
import com.couchbase.transactions.Transactions;
import com.couchbase.transactions.config.TransactionConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;

@Configuration
@EnableCouchbaseRepositories(basePackages = {"com.smartdiscover.repository"})
@EnableReactiveCouchbaseRepositories(basePackages = {"com.smartdiscover.repository"})
public class CouchConfig extends AbstractCouchbaseConfiguration {

    @Bean
    public Transactions transactions(final Cluster couchbaseCluster) {
        return Transactions.create(
                couchbaseCluster,
                TransactionConfigBuilder.create().durabilityLevel(TransactionDurabilityLevel.NONE).build()
        );
    }

    @Override
    public String getConnectionString() {
        return "couchbase://127.0.0.1";
    }

    @Override
    public String getUserName() {
        return "Administrator";
    }

    @Override
    public String getPassword() {
        return "educative@2023";
    }

    @Override
    public String getBucketName() {
        return "educative-bucket";
    }

}
