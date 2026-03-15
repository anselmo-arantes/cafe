package com.anselmo.ecommerce.order.mapper;

import com.anselmo.ecommerce.order.domain.Order;
import com.anselmo.ecommerce.order.dto.OrderItemResponse;
import com.anselmo.ecommerce.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .checkoutId(order.getCheckoutId())
                .customerEmail(order.getCustomerEmail())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .status(order.getStatus())
                .paymentId(order.getPaymentId())
                .items(parseItems(order.getItemSnapshots()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public List<String> toSnapshots(List<OrderItemResponse> items) {
        return items.stream()
                .map(item -> "%s|%d|%s|%s".formatted(item.sku(), item.quantity(), item.unitPrice(), item.lineTotal()))
                .toList();
    }

    private List<OrderItemResponse> parseItems(List<String> snapshots) {
        List<OrderItemResponse> items = new ArrayList<>();
        if (snapshots == null) {
            return items;
        }
        for (String row : snapshots) {
            String[] p = row.split("\\|");
            if (p.length == 4) {
                items.add(OrderItemResponse.builder()
                        .sku(p[0])
                        .quantity(Integer.parseInt(p[1]))
                        .unitPrice(new BigDecimal(p[2]))
                        .lineTotal(new BigDecimal(p[3]))
                        .build());
            }
        }
        return items;
    }
}
