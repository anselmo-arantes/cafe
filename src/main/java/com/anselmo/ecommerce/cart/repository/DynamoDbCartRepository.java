package com.anselmo.ecommerce.cart.repository;

import com.anselmo.ecommerce.cart.domain.CartItem;
import com.anselmo.ecommerce.catalog.config.AwsDynamoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbCartRepository implements CartRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final AwsDynamoProperties properties;

    @Override
    public Optional<CartItem> findItem(String cartId, String sku) {
        CartItem item = table().getItem(Key.builder().partitionValue(cartId).sortValue(sku).build());
        return Optional.ofNullable(item);
    }

    @Override
    public List<CartItem> findItemsByCartId(String cartId) {
        return table().query(r -> r.queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(cartId).build())))
                .items()
                .stream()
                .toList();
    }

    @Override
    public CartItem save(CartItem item) {
        table().putItem(item);
        return item;
    }

    @Override
    public void deleteItem(String cartId, String sku) {
        table().deleteItem(Key.builder().partitionValue(cartId).sortValue(sku).build());
    }

    @Override
    public void deleteAll(String cartId) {
        List<CartItem> items = findItemsByCartId(cartId);
        items.forEach(item -> deleteItem(item.getCartId(), item.getSku()));
    }

    private DynamoDbTable<CartItem> table() {
        return enhancedClient.table(properties.getDynamodb().getCartTableName(), TableSchema.fromBean(CartItem.class));
    }
}
