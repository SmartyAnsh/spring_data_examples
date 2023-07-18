package com.smartdiscover.repository;

import com.smartdiscover.model.ReservationQueue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationQueueRepository extends CrudRepository<ReservationQueue, String> {

    ReservationQueue findByBookName(String bookName);

}
