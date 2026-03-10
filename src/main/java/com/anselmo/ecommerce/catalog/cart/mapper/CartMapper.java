package com.anselmo.ecommerce.catalog.cart.mapper;

import com.anselmo.ecommerce.catalog.cart.domain.CartItem;
import com.anselmo.ecommerce.catalog.cart.dto.CartItemResponse;
import com.anselmo.ecommerce.catalog.cart.dto.CartResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(String cartId, List<CartItem> items) {
        List<CartItemResponse> cartItems = items.stream().map(this::toItemResponse).toList();
        BigDecimal subtotal = cartItems.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Instant updatedAt = items.stream()
                .map(CartItem::getUpdatedAt)
                .filter(java.util.Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(null);

        String currency = items.isEmpty() ? "BRL" : items.getFirst().getCurrency();

        return CartResponse.builder()
                .cartId(cartId)
                .items(cartItems)
                .subtotal(subtotal)
                .currency(currency)
                .updatedAt(updatedAt)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .sku(item.getSku())
                .name(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .currency(item.getCurrency())
                .lineTotal(lineTotal)
                .build();
    }
}
