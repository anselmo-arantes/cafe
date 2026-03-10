package com.anselmo.ecommerce.catalog.inventory.repository;

import com.anselmo.ecommerce.catalog.inventory.domain.InventoryItem;

import java.util.Optional;

public interface InventoryRepository {

    Optional<InventoryItem> findBySku(String sku);

    InventoryItem save(InventoryItem item);
}
