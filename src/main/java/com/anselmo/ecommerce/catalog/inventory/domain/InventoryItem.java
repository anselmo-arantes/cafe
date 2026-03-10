package com.anselmo.ecommerce.catalog.inventory.domain;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@Getter
@Setter
@DynamoDbBean
public class InventoryItem {

    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Instant updatedAt;

    @DynamoDbPartitionKey
    public String getSku() {
        return sku;
    }

    public int getSalableQuantity() {
        int available = availableQuantity == null ? 0 : availableQuantity;
        int reserved = reservedQuantity == null ? 0 : reservedQuantity;
        return available - reserved;
    }
}
