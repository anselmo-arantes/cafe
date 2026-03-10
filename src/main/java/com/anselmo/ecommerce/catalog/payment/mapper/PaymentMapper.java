package com.anselmo.ecommerce.catalog.payment.mapper;

import com.anselmo.ecommerce.catalog.payment.domain.Payment;
import com.anselmo.ecommerce.catalog.payment.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .providerReference(payment.getProviderReference())
                .reservationIds(payment.getReservationIds())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
