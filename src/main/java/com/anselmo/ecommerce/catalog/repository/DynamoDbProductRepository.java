package com.anselmo.ecommerce.catalog.repository;

import com.anselmo.ecommerce.catalog.config.AwsDynamoProperties;
import com.anselmo.ecommerce.catalog.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbProductRepository implements ProductRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final AwsDynamoProperties properties;

    @Override
    public Product save(Product product) {
        table().putItem(product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        Product item = table().getItem(Key.builder().partitionValue(id).build());
        return Optional.ofNullable(item);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        DynamoDbIndex<Product> index = table().index(properties.getDynamodb().getSkuIndexName());
        return index.query(r -> r.queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(sku).build())))
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return table().scan().items().stream().toList();
    }

    private DynamoDbTable<Product> table() {
        return enhancedClient.table(properties.getDynamodb().getTableName(), TableSchema.fromBean(Product.class));
    }
}
