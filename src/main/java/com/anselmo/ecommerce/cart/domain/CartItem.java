package com.anselmo.ecommerce.cart.domain;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@DynamoDbBean
public class CartItem {

    private String cartId;
    private String sku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String currency;
    private String productName;
    private Instant createdAt;
    private Instant updatedAt;

    @DynamoDbPartitionKey
    public String getCartId() {
        return cartId;
    }

    @DynamoDbSortKey
    public String getSku() {
        return sku;
    }
}
