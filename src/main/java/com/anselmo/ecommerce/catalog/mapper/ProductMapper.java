package com.anselmo.ecommerce.catalog.mapper;

import com.anselmo.ecommerce.catalog.domain.Product;
import com.anselmo.ecommerce.catalog.dto.AdminProductResponse;
import com.anselmo.ecommerce.catalog.dto.CreateProductRequest;
import com.anselmo.ecommerce.catalog.dto.ProductAvailabilityResponse;
import com.anselmo.ecommerce.catalog.dto.PublicProductResponse;
import com.anselmo.ecommerce.catalog.dto.UpdateProductRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ProductMapper {

    public Product toNewProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setShortDescription(request.getShortDescription());
        product.setFullDescription(request.getFullDescription());
        product.setPrice(request.getPrice());
        product.setCurrency(request.getCurrency());
        product.setActive(request.getActive());
        product.setStockQuantity(request.getStockQuantity());
        product.setMainImageUrl(request.getMainImageUrl());
        product.setImageUrls(request.getImageUrls());
        Instant now = Instant.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        return product;
    }

    public void updateFromRequest(Product product, UpdateProductRequest request) {
        product.setName(request.getName());
        product.setShortDescription(request.getShortDescription());
        product.setFullDescription(request.getFullDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setActive(request.getActive());
        product.setMainImageUrl(request.getMainImageUrl());
        product.setImageUrls(request.getImageUrls());
        product.setUpdatedAt(Instant.now());
    }

    public PublicProductResponse toPublicResponse(Product product) {
        return PublicProductResponse.builder()
                .sku(product.getSku())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .fullDescription(product.getFullDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .available(product.isAvailable())
                .mainImageUrl(product.getMainImageUrl())
                .imageUrls(product.getImageUrls())
                .build();
    }

    public ProductAvailabilityResponse toAvailabilityResponse(Product product) {
        return ProductAvailabilityResponse.builder()
                .sku(product.getSku())
                .active(Boolean.TRUE.equals(product.getActive()))
                .stockQuantity(product.getStockQuantity() == null ? 0 : product.getStockQuantity())
                .available(product.isAvailable())
                .build();
    }

    public AdminProductResponse toAdminResponse(Product product) {
        return AdminProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .fullDescription(product.getFullDescription())
                .price(product.getPrice())
                .currency(product.getCurrency())
                .active(Boolean.TRUE.equals(product.getActive()))
                .stockQuantity(product.getStockQuantity() == null ? 0 : product.getStockQuantity())
                .mainImageUrl(product.getMainImageUrl())
                .imageUrls(product.getImageUrls())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .available(product.isAvailable())
                .build();
    }
}
