package com.smartdiscover.repository;

import com.smartdiscover.entity.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomCustomerRepository {

    Customer findCustomerCustom(String firstName, String lastName);

}