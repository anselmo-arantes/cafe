package com.anselmo.ecommerce.catalog.config;

import com.anselmo.ecommerce.catalog.domain.Product;
import com.anselmo.ecommerce.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedPortableCoffeeMaker(ProductRepository productRepository,
                                              @Value("${app.seed-enabled:false}") boolean seedEnabled) {
        return args -> {
            if (!seedEnabled || productRepository.findBySku("CAFETEIRA-PORTATIL-001").isPresent()) {
                return;
            }

            Product product = new Product();
            product.setId(UUID.randomUUID().toString());
            product.setSku("CAFETEIRA-PORTATIL-001");
            product.setName("Cafeteira Portátil");
            product.setShortDescription("Cafeteira portátil para preparo rápido em qualquer lugar.");
            product.setFullDescription("Modelo portátil, compacto e ideal para viagens.");
            product.setPrice(new BigDecimal("249.90"));
            product.setCurrency("BRL");
            product.setActive(true);
            product.setStockQuantity(25);
            product.setMainImageUrl("https://cdn.exemplo.com/images/cafeteira-main.jpg");
            product.setImageUrls(List.of(
                    "https://cdn.exemplo.com/images/cafeteira-main.jpg",
                    "https://cdn.exemplo.com/images/cafeteira-side.jpg"
            ));
            Instant now = Instant.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            productRepository.save(product);
        };
    }
}
