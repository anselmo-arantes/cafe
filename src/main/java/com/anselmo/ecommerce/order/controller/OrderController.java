package com.anselmo.ecommerce.order.controller;

import com.anselmo.ecommerce.order.dto.OrderResponse;
import com.anselmo.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.anselmo.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/api/v1/orders/{orderId}")
    @Operation(summary = "Get order by id")
    public OrderResponse getById(@PathVariable String orderId) {
        return orderService.getById(orderId);
    }

    @GetMapping("/api/v1/admin/orders")
    @Operation(summary = "List all orders")
    public List<OrderResponse> listAll() {
        return orderService.listAll();
    }

    @PatchMapping("/api/v1/orders/{orderId}/status")
    @Operation(summary = "Update order status")
    public OrderResponse updateStatus(@PathVariable String orderId, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(orderId, request);
    }
}
