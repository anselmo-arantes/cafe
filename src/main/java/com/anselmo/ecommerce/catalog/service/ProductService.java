package com.anselmo.ecommerce.catalog.service;

import com.anselmo.ecommerce.catalog.domain.Product;
import com.anselmo.ecommerce.catalog.dto.AdminProductResponse;
import com.anselmo.ecommerce.catalog.dto.CreateProductRequest;
import com.anselmo.ecommerce.catalog.dto.ProductAvailabilityResponse;
import com.anselmo.ecommerce.catalog.dto.PublicProductResponse;
import com.anselmo.ecommerce.catalog.dto.UpdatePriceRequest;
import com.anselmo.ecommerce.catalog.dto.UpdateProductRequest;
import com.anselmo.ecommerce.catalog.dto.UpdateStatusRequest;
import com.anselmo.ecommerce.catalog.dto.UpdateStockRequest;
import com.anselmo.ecommerce.catalog.exception.BusinessValidationException;
import com.anselmo.ecommerce.catalog.exception.ProductNotFoundException;
import com.anselmo.ecommerce.catalog.mapper.ProductMapper;
import com.anselmo.ecommerce.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String DEFAULT_CURRENCY = "BRL";
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PublicProductResponse getPublicProductBySku(String sku) {
        Product product = getBySku(sku);
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new ProductNotFoundException("Produto não encontrado");
        }
        return productMapper.toPublicResponse(product);
    }

    public ProductAvailabilityResponse getAvailabilityBySku(String sku) {
        return productMapper.toAvailabilityResponse(getBySku(sku));
    }

    public AdminProductResponse createProduct(CreateProductRequest request) {
        validateUniqueSku(request.getSku());
        Product product = productMapper.toNewProduct(request);
        product.setCurrency(DEFAULT_CURRENCY);
        return productMapper.toAdminResponse(productRepository.save(product));
    }

    public AdminProductResponse updateProduct(String id, UpdateProductRequest request) {
        Product existing = getById(id);
        productMapper.updateFromRequest(existing, request);
        existing.setCurrency(DEFAULT_CURRENCY);
        return productMapper.toAdminResponse(productRepository.save(existing));
    }

    public AdminProductResponse updatePrice(String id, UpdatePriceRequest request) {
        Product existing = getById(id);
        existing.setPrice(request.getPrice());
        existing.setUpdatedAt(Instant.now());
        return productMapper.toAdminResponse(productRepository.save(existing));
    }

    public AdminProductResponse updateStock(String id, UpdateStockRequest request) {
        Product existing = getById(id);
        existing.setStockQuantity(request.getStockQuantity());
        existing.setUpdatedAt(Instant.now());
        return productMapper.toAdminResponse(productRepository.save(existing));
    }

    public AdminProductResponse updateStatus(String id, UpdateStatusRequest request) {
        Product existing = getById(id);
        existing.setActive(request.getActive());
        existing.setUpdatedAt(Instant.now());
        return productMapper.toAdminResponse(productRepository.save(existing));
    }

    public List<AdminProductResponse> listAdminProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toAdminResponse)
                .toList();
    }

    private Product getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));
    }

    private Product getBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado"));
    }

    private void validateUniqueSku(String sku) {
        if (productRepository.findBySku(sku).isPresent()) {
            throw new BusinessValidationException("SKU já cadastrado");
        }
    }
}
