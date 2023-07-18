package com.smartdiscover.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

@RedisHash("ReservationQueue")
@Data
public class ReservationQueue implements Serializable {

    private String id;

    @Indexed
    private String bookName;

    private List<User> userList;

}
