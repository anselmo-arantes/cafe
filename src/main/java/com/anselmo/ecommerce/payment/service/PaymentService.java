package com.anselmo.ecommerce.payment.service;

import com.anselmo.ecommerce.catalog.exception.BusinessValidationException;
import com.anselmo.ecommerce.inventory.service.InventoryService;
import com.anselmo.ecommerce.order.service.OrderService;
import com.anselmo.ecommerce.payment.domain.Payment;
import com.anselmo.ecommerce.payment.domain.PaymentStatus;
import com.anselmo.ecommerce.payment.dto.CreatePaymentRequest;
import com.anselmo.ecommerce.payment.dto.PaymentResponse;
import com.anselmo.ecommerce.payment.dto.PaymentWebhookRequest;
import com.anselmo.ecommerce.payment.exception.PaymentNotFoundException;
import com.anselmo.ecommerce.payment.mapper.PaymentMapper;
import com.anselmo.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderService orderService;
    private final InventoryService inventoryService;

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        orderService.getById(request.getOrderId());

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.PENDING.name());
        payment.setProviderReference("mock-" + System.currentTimeMillis());
        payment.setReservationIds(request.getReservationIds());
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);
        orderService.bindPayment(request.getOrderId(), payment.getPaymentId());
        return paymentMapper.toResponse(payment);
    }

    public PaymentResponse getById(String paymentId) {
        return paymentMapper.toResponse(find(paymentId));
    }

    public PaymentResponse processWebhook(PaymentWebhookRequest request) {
        Payment payment = find(request.getPaymentId());
        PaymentStatus currentStatus = PaymentStatus.valueOf(payment.getStatus());
        PaymentStatus nextStatus = PaymentStatus.valueOf(request.getStatus());

        if (currentStatus == nextStatus) {
            return paymentMapper.toResponse(payment);
        }

        if (currentStatus == PaymentStatus.PAID && nextStatus != PaymentStatus.PAID) {
            throw new BusinessValidationException("Transição inválida para pagamento já confirmado");
        }

        payment.setStatus(nextStatus.name());
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        if (nextStatus == PaymentStatus.PAID) {
            orderService.markPaid(payment.getOrderId());
            payment.getReservationIds().forEach(inventoryService::confirmReservation);
        } else if (nextStatus == PaymentStatus.FAILED) {
            orderService.markFailed(payment.getOrderId());
            payment.getReservationIds().forEach(inventoryService::releaseReservation);
        } else if (nextStatus == PaymentStatus.CANCELED) {
            orderService.markCanceled(payment.getOrderId());
            payment.getReservationIds().forEach(inventoryService::releaseReservation);
        }

        return paymentMapper.toResponse(payment);
    }

    private Payment find(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado"));
    }
}
