package com.anselmo.ecommerce.catalog.inventory.domain;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@Getter
@Setter
@DynamoDbBean
public class InventoryReservation {

    private String reservationId;
    private String sku;
    private Integer quantity;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    @DynamoDbPartitionKey
    public String getReservationId() {
        return reservationId;
    }
}
