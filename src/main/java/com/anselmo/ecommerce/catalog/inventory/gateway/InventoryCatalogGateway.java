package com.anselmo.ecommerce.catalog.inventory.gateway;

public interface InventoryCatalogGateway {

    boolean skuExists(String sku);
}
