package com.anselmo.ecommerce.order.service;

import com.anselmo.ecommerce.catalog.exception.BusinessValidationException;
import com.anselmo.ecommerce.order.domain.Order;
import com.anselmo.ecommerce.order.domain.OrderStatus;
import com.anselmo.ecommerce.order.dto.OrderItemResponse;
import com.anselmo.ecommerce.order.dto.OrderResponse;
import com.anselmo.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.anselmo.ecommerce.order.exception.OrderNotFoundException;
import com.anselmo.ecommerce.order.mapper.OrderMapper;
import com.anselmo.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderResponse createOrder(String checkoutId,
                                     String customerEmail,
                                     List<OrderItemResponse> items,
                                     BigDecimal total,
                                     String currency) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCheckoutId(checkoutId);
        order.setCustomerEmail(customerEmail);
        order.setTotalAmount(total);
        order.setCurrency(currency);
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());
        order.setItemSnapshots(orderMapper.toSnapshots(items));
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse getById(String orderId) {
        return orderMapper.toResponse(find(orderId));
    }

    public List<OrderResponse> listAll() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    public OrderResponse updateStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = find(orderId);
        validateTransition(order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());
        order.setUpdatedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse bindPayment(String orderId, String paymentId) {
        Order order = find(orderId);
        order.setPaymentId(paymentId);
        order.setUpdatedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse markPaid(String orderId) {
        return setStatus(orderId, OrderStatus.PAID.name());
    }

    public OrderResponse markFailed(String orderId) {
        return setStatus(orderId, OrderStatus.FAILED.name());
    }

    public OrderResponse markCanceled(String orderId) {
        return setStatus(orderId, OrderStatus.CANCELED.name());
    }

    private OrderResponse setStatus(String orderId, String status) {
        Order order = find(orderId);
        validateTransition(order.getStatus(), status);
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private void validateTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null || currentStatus.equals(targetStatus)) {
            return;
        }

        if (OrderStatus.CANCELED.name().equals(currentStatus) || OrderStatus.FAILED.name().equals(currentStatus)) {
            throw new BusinessValidationException("Transição de status inválida para pedido finalizado");
        }

        if (OrderStatus.PAID.name().equals(currentStatus)
                && (OrderStatus.CANCELED.name().equals(targetStatus) || OrderStatus.FAILED.name().equals(targetStatus))) {
            throw new BusinessValidationException("Transição de status inválida para pedido pago");
        }
    }

    private Order find(String id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Pedido não encontrado"));
    }
}
