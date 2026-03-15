package com.anselmo.ecommerce.order.repository;

import com.anselmo.ecommerce.order.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(String orderId);

    List<Order> findAll();
}
