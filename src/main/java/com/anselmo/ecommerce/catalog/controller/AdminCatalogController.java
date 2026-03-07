package com.anselmo.ecommerce.catalog.controller;

import com.anselmo.ecommerce.catalog.dto.AdminProductResponse;
import com.anselmo.ecommerce.catalog.dto.CreateProductRequest;
import com.anselmo.ecommerce.catalog.dto.UpdatePriceRequest;
import com.anselmo.ecommerce.catalog.dto.UpdateProductRequest;
import com.anselmo.ecommerce.catalog.dto.UpdateStatusRequest;
import com.anselmo.ecommerce.catalog.dto.UpdateStockRequest;
import com.anselmo.ecommerce.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/catalog/products")
@RequiredArgsConstructor
public class AdminCatalogController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create catalog product")
    public AdminProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details")
    public AdminProductResponse updateProduct(@PathVariable String id, @Valid @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @PatchMapping("/{id}/price")
    @Operation(summary = "Update product price")
    public AdminProductResponse updatePrice(@PathVariable String id, @Valid @RequestBody UpdatePriceRequest request) {
        return productService.updatePrice(id, request);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock")
    public AdminProductResponse updateStock(@PathVariable String id, @Valid @RequestBody UpdateStockRequest request) {
        return productService.updateStock(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate/deactivate product")
    public AdminProductResponse updateStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
        return productService.updateStatus(id, request);
    }

    @GetMapping
    @Operation(summary = "List all products")
    public List<AdminProductResponse> listProducts() {
        return productService.listAdminProducts();
    }
}
