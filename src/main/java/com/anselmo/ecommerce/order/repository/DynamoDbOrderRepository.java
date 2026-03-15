package com.anselmo.ecommerce.order.repository;

import com.anselmo.ecommerce.catalog.config.AwsDynamoProperties;
import com.anselmo.ecommerce.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbOrderRepository implements OrderRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final AwsDynamoProperties properties;

    @Override
    public Order save(Order order) {
        table().putItem(order);
        return order;
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(table().getItem(Key.builder().partitionValue(orderId).build()));
    }

    @Override
    public List<Order> findAll() {
        return table().scan().items().stream().toList();
    }

    private DynamoDbTable<Order> table() {
        return enhancedClient.table(properties.getDynamodb().getOrderTableName(), TableSchema.fromBean(Order.class));
    }
}
