package com.anselmo.ecommerce.order.service;

import com.anselmo.ecommerce.catalog.exception.BusinessValidationException;
import com.anselmo.ecommerce.order.domain.Order;
import com.anselmo.ecommerce.order.dto.OrderItemResponse;
import com.anselmo.ecommerce.order.mapper.OrderMapper;
import com.anselmo.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {

    private OrderService orderService;
    private final Map<String, Order> db = new HashMap<>();

    @BeforeEach
    void setUp() {
        OrderRepository orderRepository = new OrderRepository() {
            @Override
            public Order save(Order order) {
                db.put(order.getOrderId(), order);
                return order;
            }

            @Override
            public Optional<Order> findById(String orderId) {
                return Optional.ofNullable(db.get(orderId));
            }

            @Override
            public List<Order> findAll() {
                return db.values().stream().toList();
            }
        };
        orderService = new OrderService(orderRepository, new OrderMapper());
    }

    @Test
    void shouldCreateOrder() {
        var response = orderService.createOrder("c1", "cliente@x.com",
                List.of(OrderItemResponse.builder().sku("CAFETEIRA-PORTATIL-001").quantity(1).unitPrice(new BigDecimal("249.90")).lineTotal(new BigDecimal("249.90")).build()),
                new BigDecimal("249.90"), "BRL");
        assertThat(response.status()).isEqualTo("PENDING_PAYMENT");
    }

    @Test
    void shouldRejectCanceledOrderToPaidTransition() {
        var created = orderService.createOrder("c1", "cliente@x.com", List.of(), BigDecimal.ONE, "BRL");
        orderService.markCanceled(created.orderId());

        assertThatThrownBy(() -> orderService.markPaid(created.orderId()))
                .isInstanceOf(BusinessValidationException.class);
    }
}
