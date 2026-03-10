package com.anselmo.ecommerce.catalog.mapper;

import com.anselmo.ecommerce.catalog.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void shouldMapPublicResponse() {
        Product product = new Product();
        product.setSku("CAFETEIRA-PORTATIL-001");
        product.setName("Cafeteira Portátil");
        product.setPrice(new BigDecimal("249.90"));
        product.setCurrency("BRL");
        product.setActive(true);
        product.setStockQuantity(2);

        var response = mapper.toPublicResponse(product);

        assertThat(response.sku()).isEqualTo("CAFETEIRA-PORTATIL-001");
        assertThat(response.available()).isTrue();
    }
}
