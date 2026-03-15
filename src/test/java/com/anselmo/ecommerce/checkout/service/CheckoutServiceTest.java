package com.anselmo.ecommerce.checkout.service;

import com.anselmo.ecommerce.cart.dto.CartItemResponse;
import com.anselmo.ecommerce.cart.dto.CartResponse;
import com.anselmo.ecommerce.cart.service.CartService;
import com.anselmo.ecommerce.checkout.dto.CheckoutRequest;
import com.anselmo.ecommerce.inventory.dto.CreateReservationRequest;
import com.anselmo.ecommerce.inventory.dto.InventoryReservationResponse;
import com.anselmo.ecommerce.inventory.service.InventoryService;
import com.anselmo.ecommerce.order.dto.OrderItemResponse;
import com.anselmo.ecommerce.order.dto.OrderResponse;
import com.anselmo.ecommerce.order.service.OrderService;
import com.anselmo.ecommerce.payment.dto.CreatePaymentRequest;
import com.anselmo.ecommerce.payment.dto.PaymentResponse;
import com.anselmo.ecommerce.payment.service.PaymentService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CheckoutServiceTest {

    @Test
    void shouldCheckoutSuccessfully() {
        CartService cartService = cartService();

        InventoryService inventoryService = new InventoryService(null, null, null, null) {
            @Override
            public InventoryReservationResponse reserve(CreateReservationRequest request) {
                return InventoryReservationResponse.builder()
                        .reservationId(request.getReservationId())
                        .sku(request.getSku())
                        .quantity(request.getQuantity())
                        .status("RESERVED")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
            }
        };

        OrderService orderService = new OrderService(null, null) {
            @Override
            public OrderResponse createOrder(String checkoutId, String customerEmail, List<OrderItemResponse> items, BigDecimal total, String currency) {
                return OrderResponse.builder()
                        .orderId("o1")
                        .checkoutId(checkoutId)
                        .customerEmail(customerEmail)
                        .totalAmount(total)
                        .currency(currency)
                        .status("PENDING_PAYMENT")
                        .items(items)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
            }
        };

        PaymentService paymentService = new PaymentService(null, null, null, null) {
            @Override
            public PaymentResponse createPayment(CreatePaymentRequest request) {
                return PaymentResponse.builder()
                        .paymentId("p1")
                        .orderId(request.getOrderId())
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
                        .status("PENDING")
                        .reservationIds(request.getReservationIds())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
            }
        };

        CheckoutService checkoutService = new CheckoutService(cartService, inventoryService, orderService, paymentService);
        CheckoutRequest request = new CheckoutRequest();
        request.setCartId("cart-1");
        request.setCustomerEmail("cliente@exemplo.com");

        var response = checkoutService.checkout(request);

        assertThat(response.orderId()).isEqualTo("o1");
        assertThat(response.paymentId()).isEqualTo("p1");
    }

    @Test
    void shouldRollbackReservationsAndMarkOrderFailedWhenPaymentCreationFails() {
        CartService cartService = cartService();
        List<String> releasedReservations = new ArrayList<>();
        AtomicBoolean orderMarkedFailed = new AtomicBoolean(false);

        InventoryService inventoryService = new InventoryService(null, null, null, null) {
            @Override
            public InventoryReservationResponse reserve(CreateReservationRequest request) {
                return InventoryReservationResponse.builder().reservationId(request.getReservationId()).status("RESERVED").build();
            }

            @Override
            public InventoryReservationResponse releaseReservation(String reservationId) {
                releasedReservations.add(reservationId);
                return InventoryReservationResponse.builder().reservationId(reservationId).status("RELEASED").build();
            }
        };

        OrderService orderService = new OrderService(null, null) {
            @Override
            public OrderResponse createOrder(String checkoutId, String customerEmail, List<OrderItemResponse> items, BigDecimal total, String currency) {
                return OrderResponse.builder().orderId("o-fail").checkoutId(checkoutId).customerEmail(customerEmail).totalAmount(total).currency(currency).status("PENDING_PAYMENT").items(items).build();
            }

            @Override
            public OrderResponse markFailed(String orderId) {
                orderMarkedFailed.set(true);
                return OrderResponse.builder().orderId(orderId).status("FAILED").build();
            }
        };

        PaymentService paymentService = new PaymentService(null, null, null, null) {
            @Override
            public PaymentResponse createPayment(CreatePaymentRequest request) {
                throw new RuntimeException("payment timeout");
            }
        };

        CheckoutService checkoutService = new CheckoutService(cartService, inventoryService, orderService, paymentService);
        CheckoutRequest request = new CheckoutRequest();
        request.setCartId("cart-1");
        request.setCustomerEmail("cliente@exemplo.com");

        assertThatThrownBy(() -> checkoutService.checkout(request)).isInstanceOf(RuntimeException.class);
        assertThat(releasedReservations).hasSize(1);
        assertThat(orderMarkedFailed).isTrue();
    }

    private CartService cartService() {
        return new CartService(null, null, null) {
            @Override
            public CartResponse getCart(String cartId) {
                return CartResponse.builder()
                        .cartId(cartId)
                        .items(List.of(CartItemResponse.builder()
                                .sku("CAFETEIRA-PORTATIL-001")
                                .name("Cafeteira")
                                .quantity(1)
                                .unitPrice(new BigDecimal("249.90"))
                                .currency("BRL")
                                .lineTotal(new BigDecimal("249.90"))
                                .build()))
                        .subtotal(new BigDecimal("249.90"))
                        .currency("BRL")
                        .updatedAt(Instant.now())
                        .build();
            }
        };
    }
}
