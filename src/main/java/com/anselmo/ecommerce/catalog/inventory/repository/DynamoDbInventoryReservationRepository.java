package com.anselmo.ecommerce.catalog.inventory.repository;

import com.anselmo.ecommerce.catalog.config.AwsDynamoProperties;
import com.anselmo.ecommerce.catalog.inventory.domain.InventoryReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbInventoryReservationRepository implements InventoryReservationRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final AwsDynamoProperties properties;

    @Override
    public Optional<InventoryReservation> findById(String reservationId) {
        InventoryReservation reservation = table().getItem(Key.builder().partitionValue(reservationId).build());
        return Optional.ofNullable(reservation);
    }

    @Override
    public InventoryReservation save(InventoryReservation reservation) {
        table().putItem(reservation);
        return reservation;
    }

    private DynamoDbTable<InventoryReservation> table() {
        return enhancedClient.table(properties.getDynamodb().getInventoryReservationTableName(), TableSchema.fromBean(InventoryReservation.class));
    }
}
