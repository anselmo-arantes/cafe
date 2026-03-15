package com.anselmo.ecommerce.catalog.cart.controller;

import com.anselmo.ecommerce.catalog.cart.dto.AddCartItemRequest;
import com.anselmo.ecommerce.catalog.cart.dto.CartResponse;
import com.anselmo.ecommerce.catalog.cart.dto.UpdateCartItemRequest;
import com.anselmo.ecommerce.catalog.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{cartId}")
    @Operation(summary = "Get cart by id")
    public CartResponse getCart(@PathVariable String cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add item to cart")
    public CartResponse addItem(@PathVariable String cartId, @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(cartId, request);
    }

    @PatchMapping("/{cartId}/items/{sku}")
    @Operation(summary = "Update cart item quantity")
    public CartResponse updateItem(@PathVariable String cartId,
                                   @PathVariable String sku,
                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(cartId, sku, request);
    }

    @DeleteMapping("/{cartId}/items/{sku}")
    @Operation(summary = "Remove cart item")
    public CartResponse removeItem(@PathVariable String cartId, @PathVariable String sku) {
        return cartService.removeItem(cartId, sku);
    }

    @DeleteMapping("/{cartId}/items")
    @Operation(summary = "Clear cart")
    public CartResponse clearCart(@PathVariable String cartId) {
        return cartService.clearCart(cartId);
    }
}
