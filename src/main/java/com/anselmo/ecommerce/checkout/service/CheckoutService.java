package com.anselmo.ecommerce.checkout.service;

import com.anselmo.ecommerce.cart.dto.CartResponse;
import com.anselmo.ecommerce.cart.service.CartService;
import com.anselmo.ecommerce.checkout.dto.CheckoutRequest;
import com.anselmo.ecommerce.checkout.dto.CheckoutResponse;
import com.anselmo.ecommerce.checkout.exception.CheckoutValidationException;
import com.anselmo.ecommerce.inventory.dto.CreateReservationRequest;
import com.anselmo.ecommerce.inventory.service.InventoryService;
import com.anselmo.ecommerce.order.dto.OrderItemResponse;
import com.anselmo.ecommerce.order.dto.OrderResponse;
import com.anselmo.ecommerce.order.service.OrderService;
import com.anselmo.ecommerce.payment.dto.CreatePaymentRequest;
import com.anselmo.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartService cartService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public CheckoutResponse checkout(CheckoutRequest request) {
        CartResponse cart = cartService.getCart(request.getCartId());
        if (cart.items() == null || cart.items().isEmpty()) {
            throw new CheckoutValidationException("Carrinho vazio não pode finalizar checkout");
        }

        String checkoutId = UUID.randomUUID().toString();
        List<String> reservationIds = new ArrayList<>();

        OrderResponse order = null;
        try {
            for (var item : cart.items()) {
                String reservationId = checkoutId + "-" + item.sku();
                CreateReservationRequest reserveRequest = new CreateReservationRequest();
                reserveRequest.setReservationId(reservationId);
                reserveRequest.setSku(item.sku());
                reserveRequest.setQuantity(item.quantity());
                inventoryService.reserve(reserveRequest);
                reservationIds.add(reservationId);
            }

            List<OrderItemResponse> items = cart.items().stream().map(item -> OrderItemResponse.builder()
                    .sku(item.sku())
                    .quantity(item.quantity())
                    .unitPrice(item.unitPrice())
                    .lineTotal(item.lineTotal())
                    .build()).toList();

            order = orderService.createOrder(checkoutId,
                    request.getCustomerEmail(),
                    items,
                    cart.subtotal(),
                    cart.currency());

            CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
            paymentRequest.setOrderId(order.orderId());
            paymentRequest.setAmount(order.totalAmount());
            paymentRequest.setCurrency(order.currency());
            paymentRequest.setReservationIds(reservationIds);

            var payment = paymentService.createPayment(paymentRequest);

            return CheckoutResponse.builder()
                    .checkoutId(checkoutId)
                    .orderId(order.orderId())
                    .paymentId(payment.paymentId())
                    .status(order.status())
                    .amount(order.totalAmount())
                    .currency(order.currency())
                    .reservationIds(reservationIds)
                    .build();
        } catch (RuntimeException checkoutFailure) {
            reservationIds.forEach(inventoryService::releaseReservation);
            if (order != null) {
                orderService.markFailed(order.orderId());
            }
            throw checkoutFailure;
        }
    }
}
