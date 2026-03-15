package com.anselmo.ecommerce.catalog.inventory.repository;

import com.anselmo.ecommerce.catalog.config.AwsDynamoProperties;
import com.anselmo.ecommerce.catalog.inventory.domain.InventoryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbInventoryRepository implements InventoryRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final AwsDynamoProperties properties;

    @Override
    public Optional<InventoryItem> findBySku(String sku) {
        InventoryItem item = table().getItem(Key.builder().partitionValue(sku).build());
        return Optional.ofNullable(item);
    }

    @Override
    public InventoryItem save(InventoryItem item) {
        table().putItem(item);
        return item;
    }

    private DynamoDbTable<InventoryItem> table() {
        return enhancedClient.table(properties.getDynamodb().getInventoryTableName(), TableSchema.fromBean(InventoryItem.class));
    }
}
