package com.anselmo.ecommerce.catalog.controller;

import com.anselmo.ecommerce.catalog.dto.AdminProductResponse;
import com.anselmo.ecommerce.catalog.exception.GlobalExceptionHandler;
import com.anselmo.ecommerce.catalog.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCatalogController.class)
@Import(GlobalExceptionHandler.class)
class AdminCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void shouldCreateProduct() throws Exception {
        var response = AdminProductResponse.builder()
                .id("id-1")
                .sku("CAFETEIRA-PORTATIL-001")
                .name("Cafeteira Portátil")
                .shortDescription("desc")
                .fullDescription("full")
                .price(new BigDecimal("249.90"))
                .currency("BRL")
                .active(true)
                .stockQuantity(25)
                .mainImageUrl("https://cdn.exemplo.com/images/cafeteira-main.jpg")
                .imageUrls(List.of("https://cdn.exemplo.com/images/cafeteira-main.jpg"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .available(true)
                .build();

        when(productService.createProduct(any())).thenReturn(response);

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
