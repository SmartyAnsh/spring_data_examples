package com.smartdiscover.entity.projection;

import org.springframework.beans.factory.annotation.Value;

public interface PersonInfo {

    String getFirstName();

    @Value("#{target.lastName + ' ' + target.firstName}")
    String getFullName();

}