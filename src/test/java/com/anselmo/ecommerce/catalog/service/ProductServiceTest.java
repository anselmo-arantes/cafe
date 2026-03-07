package com.anselmo.ecommerce.catalog.service;

import com.anselmo.ecommerce.catalog.domain.Product;
import com.anselmo.ecommerce.catalog.dto.CreateProductRequest;
import com.anselmo.ecommerce.catalog.dto.UpdatePriceRequest;
import com.anselmo.ecommerce.catalog.exception.BusinessValidationException;
import com.anselmo.ecommerce.catalog.exception.ProductNotFoundException;
import com.anselmo.ecommerce.catalog.mapper.ProductMapper;
import com.anselmo.ecommerce.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
        productService = new ProductService(productRepository, mapper);
    }

    @Test
    void shouldCreateProductWithDefaultCurrency() {
        CreateProductRequest request = new CreateProductRequest();
        request.setSku("CAFETEIRA-PORTATIL-001");
        request.setName("Cafeteira Portátil");
        request.setPrice(new BigDecimal("249.90"));
        request.setActive(true);
        request.setStockQuantity(10);
        request.setCurrency("USD");

        when(productRepository.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = productService.createProduct(request);

        assertThat(response.currency()).isEqualTo("BRL");
        assertThat(response.available()).isTrue();
    }

    @Test
    void shouldFailWhenSkuAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest();
        request.setSku("CAFETEIRA-PORTATIL-001");
        request.setName("Cafeteira Portátil");
        request.setPrice(new BigDecimal("249.90"));
        request.setActive(true);
        request.setStockQuantity(10);

        when(productRepository.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(sampleProduct()));

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("SKU já cadastrado");
    }

    @Test
    void shouldThrowNotFoundForInactivePublicProduct() {
        Product inactive = sampleProduct();
        inactive.setActive(false);

        when(productRepository.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> productService.getPublicProductBySku("CAFETEIRA-PORTATIL-001"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldUpdatePrice() {
        Product product = sampleProduct();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePriceRequest request = new UpdatePriceRequest();
        request.setPrice(new BigDecimal("299.90"));

        var response = productService.updatePrice(product.getId(), request);

        assertThat(response.price()).isEqualByComparingTo("299.90");
    }

    private Product sampleProduct() {
        Product product = new Product();
        product.setId("id-1");
        product.setSku("CAFETEIRA-PORTATIL-001");
        product.setName("Cafeteira Portátil");
        product.setPrice(new BigDecimal("249.90"));
        product.setCurrency("BRL");
        product.setActive(true);
        product.setStockQuantity(10);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        return product;
    }
}
