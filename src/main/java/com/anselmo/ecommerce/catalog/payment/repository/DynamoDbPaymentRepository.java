package com.anselmo.ecommerce.catalog.payment.repository;

import com.anselmo.ecommerce.catalog.config.AwsDynamoProperties;
import com.anselmo.ecommerce.catalog.payment.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbPaymentRepository implements PaymentRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final AwsDynamoProperties properties;

    @Override
    public Payment save(Payment payment) {
        table().putItem(payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(table().getItem(Key.builder().partitionValue(paymentId).build()));
    }

    private DynamoDbTable<Payment> table() {
        return enhancedClient.table(properties.getDynamodb().getPaymentTableName(), TableSchema.fromBean(Payment.class));
    }
}
