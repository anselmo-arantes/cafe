package com.anselmo.ecommerce.catalog.cart.repository;

import com.anselmo.ecommerce.catalog.cart.domain.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartRepository {

    Optional<CartItem> findItem(String cartId, String sku);

    List<CartItem> findItemsByCartId(String cartId);

    CartItem save(CartItem item);

    void deleteItem(String cartId, String sku);

    void deleteAll(String cartId);
}
