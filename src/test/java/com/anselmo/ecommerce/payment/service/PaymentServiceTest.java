package com.anselmo.ecommerce.payment.service;

import com.anselmo.ecommerce.catalog.exception.BusinessValidationException;
import com.anselmo.ecommerce.inventory.dto.InventoryReservationResponse;
import com.anselmo.ecommerce.inventory.service.InventoryService;
import com.anselmo.ecommerce.order.dto.OrderResponse;
import com.anselmo.ecommerce.order.exception.OrderNotFoundException;
import com.anselmo.ecommerce.order.service.OrderService;
import com.anselmo.ecommerce.payment.domain.Payment;
import com.anselmo.ecommerce.payment.dto.CreatePaymentRequest;
import com.anselmo.ecommerce.payment.dto.PaymentWebhookRequest;
import com.anselmo.ecommerce.payment.mapper.PaymentMapper;
import com.anselmo.ecommerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentServiceTest {

    private PaymentService paymentService;
    private final Map<String, Payment> paymentDb = new HashMap<>();
    private final AtomicInteger confirmCalls = new AtomicInteger();

    @BeforeEach
    void setUp() {
        PaymentRepository repo = new PaymentRepository() {
            @Override
            public Payment save(Payment payment) {
                paymentDb.put(payment.getPaymentId(), payment);
                return payment;
            }

            @Override
            public Optional<Payment> findById(String paymentId) {
                return Optional.ofNullable(paymentDb.get(paymentId));
            }
        };

        Map<String, String> orderStates = new HashMap<>();
        orderStates.put("order-1", "PENDING_PAYMENT");

        OrderService orderService = new OrderService(null, null) {
            @Override
            public OrderResponse getById(String orderId) {
                if (!orderStates.containsKey(orderId)) {
                    throw new OrderNotFoundException("Pedido não encontrado");
                }
                return OrderResponse.builder().orderId(orderId).status(orderStates.get(orderId)).build();
            }

            @Override
            public OrderResponse bindPayment(String orderId, String paymentId) {
                getById(orderId);
                return OrderResponse.builder().orderId(orderId).paymentId(paymentId).status(orderStates.get(orderId)).build();
            }

            @Override
            public OrderResponse markPaid(String orderId) {
                orderStates.put(orderId, "PAID");
                return OrderResponse.builder().orderId(orderId).status("PAID").build();
            }

            @Override
            public OrderResponse markFailed(String orderId) {
                orderStates.put(orderId, "FAILED");
                return OrderResponse.builder().orderId(orderId).status("FAILED").build();
            }

            @Override
            public OrderResponse markCanceled(String orderId) {
                orderStates.put(orderId, "CANCELED");
                return OrderResponse.builder().orderId(orderId).status("CANCELED").build();
            }
        };

        InventoryService inventoryService = new InventoryService(null, null, null, null) {
            @Override
            public InventoryReservationResponse confirmReservation(String reservationId) {
                confirmCalls.incrementAndGet();
                return InventoryReservationResponse.builder().reservationId(reservationId).status("CONFIRMED").createdAt(Instant.now()).updatedAt(Instant.now()).build();
            }

            @Override
            public InventoryReservationResponse releaseReservation(String reservationId) {
                return InventoryReservationResponse.builder().reservationId(reservationId).status("RELEASED").createdAt(Instant.now()).updatedAt(Instant.now()).build();
            }
        };

        paymentService = new PaymentService(repo, new PaymentMapper(), orderService, inventoryService);
    }

    @Test
    void shouldProcessDuplicatePaidWebhookIdempotently() {
        var created = createPayment("order-1");

        PaymentWebhookRequest webhook = new PaymentWebhookRequest();
        webhook.setPaymentId(created.paymentId());
        webhook.setStatus("PAID");

        paymentService.processWebhook(webhook);
        paymentService.processWebhook(webhook);

        assertThat(confirmCalls.get()).isEqualTo(1);
    }

    @Test
    void shouldRejectFailedWebhookAfterPaid() {
        var created = createPayment("order-1");

        PaymentWebhookRequest paid = new PaymentWebhookRequest();
        paid.setPaymentId(created.paymentId());
        paid.setStatus("PAID");
        paymentService.processWebhook(paid);

        PaymentWebhookRequest failed = new PaymentWebhookRequest();
        failed.setPaymentId(created.paymentId());
        failed.setStatus("FAILED");

        assertThatThrownBy(() -> paymentService.processWebhook(failed))
                .isInstanceOf(BusinessValidationException.class);
    }

    @Test
    void shouldFailWhenCreatingPaymentWithInvalidOrderId() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setOrderId("missing-order");
        request.setAmount(BigDecimal.TEN);
        request.setCurrency("BRL");
        request.setReservationIds(List.of("r1"));

        assertThatThrownBy(() -> paymentService.createPayment(request))
                .isInstanceOf(OrderNotFoundException.class);
        assertThat(paymentDb).isEmpty();
    }

    private com.anselmo.ecommerce.payment.dto.PaymentResponse createPayment(String orderId) {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(new BigDecimal("249.90"));
        request.setCurrency("BRL");
        request.setReservationIds(List.of("r1"));
        return paymentService.createPayment(request);
    }
}
