package com.anselmo.ecommerce.catalog.payment.repository;

import com.anselmo.ecommerce.catalog.payment.domain.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String paymentId);
}
