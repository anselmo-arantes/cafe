package com.anselmo.ecommerce.catalog.controller;

import com.anselmo.ecommerce.catalog.dto.ProductAvailabilityResponse;
import com.anselmo.ecommerce.catalog.dto.PublicProductResponse;
import com.anselmo.ecommerce.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog/products")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final ProductService productService;

    @GetMapping("/{sku}")
    @Operation(summary = "Get public product details by SKU")
    public PublicProductResponse getProduct(@PathVariable String sku) {
        return productService.getPublicProductBySku(sku);
    }

    @GetMapping("/{sku}/availability")
    @Operation(summary = "Get product availability by SKU")
    public ProductAvailabilityResponse getAvailability(@PathVariable String sku) {
        return productService.getAvailabilityBySku(sku);
    }
}
