package com.anselmo.ecommerce.catalog.domain;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@DynamoDbBean
public class Product {

    private String id;
    private String sku;
    private String name;
    private String shortDescription;
    private String fullDescription;
    private BigDecimal price;
    private String currency;
    private Boolean active;
    private Integer stockQuantity;
    private String mainImageUrl;
    private List<String> imageUrls;
    private Instant createdAt;
    private Instant updatedAt;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "sku-index")
    public String getSku() {
        return sku;
    }

    @DynamoDbIgnore
    public boolean isAvailable() {
        return Boolean.TRUE.equals(active) && stockQuantity != null && stockQuantity > 0;
    }
}
