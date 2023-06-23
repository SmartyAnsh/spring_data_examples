package com.smartdiscover.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableCouchbaseRepositories(basePackages={"com.smartdiscover.repository"})
@EnableReactiveCouchbaseRepositories(basePackages={"com.smartdiscover.repository"})
//@EnableTransactionManagement
public class CouchConfig extends AbstractCouchbaseConfiguration {

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
