package com.anselmo.ecommerce.payment.repository;

import com.anselmo.ecommerce.payment.domain.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String paymentId);
}
