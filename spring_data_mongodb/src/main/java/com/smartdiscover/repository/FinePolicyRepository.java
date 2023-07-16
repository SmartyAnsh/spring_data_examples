package com.smartdiscover.repository;

import com.smartdiscover.document.FinePolicy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FinePolicyRepository extends MongoRepository<FinePolicy, String> {

    public FinePolicy findByActive(boolean active);

}