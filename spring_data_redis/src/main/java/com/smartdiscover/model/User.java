package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("User")
@Data
public class User {

    private String id;

    @Indexed
    private String firstName;

    @Indexed
    private String lastName;

}
