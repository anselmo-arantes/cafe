package com.anselmo.ecommerce.catalog.cart.gateway;

import com.anselmo.ecommerce.catalog.cart.dto.CatalogProductSnapshot;
import com.anselmo.ecommerce.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LocalCatalogGateway implements CatalogGateway {

    private final ProductRepository productRepository;

    @Override
    public Optional<CatalogProductSnapshot> findBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(product -> new CatalogProductSnapshot(
                        product.getSku(),
                        product.getName(),
                        product.getPrice(),
                        product.getCurrency(),
                        Boolean.TRUE.equals(product.getActive())
                ));
    }
}
