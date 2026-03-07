package com.anselmo.ecommerce.catalog.controller;

import com.anselmo.ecommerce.catalog.dto.ProductAvailabilityResponse;
import com.anselmo.ecommerce.catalog.dto.PublicProductResponse;
import com.anselmo.ecommerce.catalog.exception.GlobalExceptionHandler;
import com.anselmo.ecommerce.catalog.exception.ProductNotFoundException;
import com.anselmo.ecommerce.catalog.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicCatalogController.class)
@Import(GlobalExceptionHandler.class)
class PublicCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldReturnProduct() throws Exception {
        when(productService.getPublicProductBySku("CAFETEIRA-PORTATIL-001")).thenReturn(
                PublicProductResponse.builder()
                        .sku("CAFETEIRA-PORTATIL-001")
                        .name("Cafeteira Portátil")
                        .shortDescription("desc")
                        .fullDescription("full")
                        .price(new BigDecimal("249.90"))
                        .currency("BRL")
                        .available(true)
                        .mainImageUrl("https://cdn.exemplo.com/images/cafeteira-main.jpg")
                        .imageUrls(List.of("https://cdn.exemplo.com/images/cafeteira-main.jpg"))
                        .build()
        );

        mockMvc.perform(get("/api/v1/catalog/products/CAFETEIRA-PORTATIL-001").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("CAFETEIRA-PORTATIL-001"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productService.getAvailabilityBySku("MISSING"))
                .thenThrow(new ProductNotFoundException("Produto não encontrado"));

        mockMvc.perform(get("/api/v1/catalog/products/MISSING/availability"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void shouldReturnAvailability() throws Exception {
        when(productService.getAvailabilityBySku("CAFETEIRA-PORTATIL-001"))
                .thenReturn(ProductAvailabilityResponse.builder()
                        .sku("CAFETEIRA-PORTATIL-001")
                        .active(true)
                        .stockQuantity(15)
                        .available(true)
                        .build());

        mockMvc.perform(get("/api/v1/catalog/products/CAFETEIRA-PORTATIL-001/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(15));
    }
}
