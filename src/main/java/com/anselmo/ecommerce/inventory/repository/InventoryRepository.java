package com.anselmo.ecommerce.inventory.repository;

import com.anselmo.ecommerce.inventory.domain.InventoryItem;

import java.util.Optional;

public interface InventoryRepository {

    Optional<InventoryItem> findBySku(String sku);

    InventoryItem save(InventoryItem item);
}
