package com.anselmo.ecommerce.catalog.controller;

import com.anselmo.ecommerce.catalog.dto.ProductAvailabilityResponse;
import com.anselmo.ecommerce.catalog.dto.PublicProductResponse;
import com.anselmo.ecommerce.catalog.exception.GlobalExceptionHandler;
import com.anselmo.ecommerce.catalog.exception.ProductNotFoundException;
import com.anselmo.ecommerce.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublicCatalogControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductService productService = new ProductService(null, null) {
            @Override
            public PublicProductResponse getPublicProductBySku(String sku) {
                if ("CAFETEIRA-PORTATIL-001".equals(sku)) {
                    return PublicProductResponse.builder()
                            .sku("CAFETEIRA-PORTATIL-001")
                            .name("Cafeteira Portátil")
                            .shortDescription("desc")
                            .fullDescription("full")
                            .price(new BigDecimal("249.90"))
                            .currency("BRL")
                            .available(true)
                            .mainImageUrl("https://cdn.exemplo.com/images/cafeteira-main.jpg")
                            .imageUrls(List.of("https://cdn.exemplo.com/images/cafeteira-main.jpg"))
                            .build();
                }
                throw new ProductNotFoundException("Produto não encontrado");
            }

            @Override
            public ProductAvailabilityResponse getAvailabilityBySku(String sku) {
                if ("MISSING".equals(sku)) {
                    throw new ProductNotFoundException("Produto não encontrado");
                }
                return ProductAvailabilityResponse.builder()
                        .sku("CAFETEIRA-PORTATIL-001")
                        .active(true)
                        .stockQuantity(15)
                        .available(true)
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new PublicCatalogController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/products/CAFETEIRA-PORTATIL-001").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("CAFETEIRA-PORTATIL-001"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/products/MISSING/availability"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void shouldReturnAvailability() throws Exception {
        mockMvc.perform(get("/api/v1/catalog/products/CAFETEIRA-PORTATIL-001/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(15));
    }
}
