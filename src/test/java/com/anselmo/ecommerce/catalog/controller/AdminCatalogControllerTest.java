package com.anselmo.ecommerce.catalog.controller;

import com.anselmo.ecommerce.catalog.dto.AdminProductResponse;
import com.anselmo.ecommerce.catalog.dto.CreateProductRequest;
import com.anselmo.ecommerce.catalog.exception.GlobalExceptionHandler;
import com.anselmo.ecommerce.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminCatalogControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ProductService productService = new ProductService(null, null) {
            @Override
            public AdminProductResponse createProduct(CreateProductRequest request) {
                return AdminProductResponse.builder()
                        .id("id-1")
                        .sku(request.getSku())
                        .name(request.getName())
                        .shortDescription(request.getShortDescription())
                        .fullDescription(request.getFullDescription())
                        .price(request.getPrice())
                        .currency("BRL")
                        .active(true)
                        .stockQuantity(request.getStockQuantity())
                        .mainImageUrl(request.getMainImageUrl())
                        .imageUrls(request.getImageUrls())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .available(true)
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new AdminCatalogController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        String payload = """
                {
                  "sku": "CAFETEIRA-PORTATIL-001",
                  "name": "Cafeteira Portátil",
                  "shortDescription": "desc",
                  "fullDescription": "full",
                  "price": 249.90,
                  "active": true,
                  "stockQuantity": 25,
                  "mainImageUrl": "https://cdn.exemplo.com/images/cafeteira-main.jpg",
                  "imageUrls": ["https://cdn.exemplo.com/images/cafeteira-main.jpg"]
                }
                """;

        mockMvc.perform(post("/api/v1/admin/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id-1"));
    }

    @Test
    void shouldReturn400ForInvalidCreatePayload() throws Exception {
        String payload = """
                {
                  "sku": "",
                  "name": "",
                  "price": -1,
                  "active": true,
                  "stockQuantity": -1
                }
                """;

        mockMvc.perform(post("/api/v1/admin/catalog/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
