package com.anselmo.ecommerce.cart.service;

import com.anselmo.ecommerce.cart.domain.CartItem;
import com.anselmo.ecommerce.cart.dto.AddCartItemRequest;
import com.anselmo.ecommerce.cart.dto.CartResponse;
import com.anselmo.ecommerce.cart.dto.UpdateCartItemRequest;
import com.anselmo.ecommerce.cart.exception.CartItemNotFoundException;
import com.anselmo.ecommerce.cart.exception.CatalogItemNotFoundException;
import com.anselmo.ecommerce.cart.gateway.CatalogGateway;
import com.anselmo.ecommerce.cart.mapper.CartMapper;
import com.anselmo.ecommerce.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CatalogGateway catalogGateway;

    public CartResponse getCart(String cartId) {
        List<CartItem> items = cartRepository.findItemsByCartId(cartId);
        return cartMapper.toResponse(cartId, items);
    }

    public CartResponse addItem(String cartId, AddCartItemRequest request) {
        var product = catalogGateway.findBySku(request.getSku())
                .orElseThrow(() -> new CatalogItemNotFoundException("Produto não encontrado no catálogo"));

        CartItem item = cartRepository.findItem(cartId, request.getSku()).orElseGet(CartItem::new);
        item.setCartId(cartId);
        item.setSku(product.sku());
        item.setProductName(product.name());
        item.setUnitPrice(product.price());
        item.setCurrency(product.currency());
        item.setQuantity((item.getQuantity() == null ? 0 : item.getQuantity()) + request.getQuantity());
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(Instant.now());
        }
        item.setUpdatedAt(Instant.now());
        cartRepository.save(item);

        return getCart(cartId);
    }

    public CartResponse updateItem(String cartId, String sku, UpdateCartItemRequest request) {
        CartItem item = cartRepository.findItem(cartId, sku)
                .orElseThrow(() -> new CartItemNotFoundException("Item não encontrado no carrinho"));
        item.setQuantity(request.getQuantity());
        item.setUpdatedAt(Instant.now());
        cartRepository.save(item);
        return getCart(cartId);
    }

    public CartResponse removeItem(String cartId, String sku) {
        if (cartRepository.findItem(cartId, sku).isEmpty()) {
            throw new CartItemNotFoundException("Item não encontrado no carrinho");
        }
        cartRepository.deleteItem(cartId, sku);
        return getCart(cartId);
    }

    public CartResponse clearCart(String cartId) {
        cartRepository.deleteAll(cartId);
        return getCart(cartId);
    }
}
