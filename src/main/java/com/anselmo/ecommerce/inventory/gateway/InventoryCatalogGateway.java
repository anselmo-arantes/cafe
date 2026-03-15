package com.anselmo.ecommerce.inventory.gateway;

public interface InventoryCatalogGateway {

    boolean skuExists(String sku);
}
