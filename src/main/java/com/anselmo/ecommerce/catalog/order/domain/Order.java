package com.anselmo.ecommerce.catalog.order.domain;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@DynamoDbBean
public class Order {

    private String orderId;
    private String checkoutId;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private String paymentId;
    private List<String> itemSnapshots;
    private Instant createdAt;
    private Instant updatedAt;

    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }
}
