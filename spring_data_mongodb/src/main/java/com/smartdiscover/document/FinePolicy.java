package com.smartdiscover.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class FinePolicy {

    @Id
    private String id;

    private double dailyFineRate;
    private double maxFineAmount;

    private boolean active;
}

