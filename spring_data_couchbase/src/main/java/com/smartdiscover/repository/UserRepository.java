package com.smartdiscover.repository;

import com.couchbase.client.java.query.QueryScanConsistency;
import com.smartdiscover.model.User;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CouchbaseRepository<User, String> {

    @ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
    User findByFirstNameAndLastName(String firstName, String lastName);

}
