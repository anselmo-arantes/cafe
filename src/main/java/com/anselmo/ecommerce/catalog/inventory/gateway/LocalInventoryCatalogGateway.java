package com.anselmo.ecommerce.catalog.inventory.gateway;

import com.anselmo.ecommerce.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalInventoryCatalogGateway implements InventoryCatalogGateway {

    private final ProductRepository productRepository;

    @Override
    public boolean skuExists(String sku) {
        return productRepository.findBySku(sku).isPresent();
    }
}
