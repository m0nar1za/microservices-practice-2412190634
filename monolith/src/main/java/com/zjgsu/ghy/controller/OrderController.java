package com.zjgsu.ghy.controller;

import com.zjgsu.ghy.dto.OrderRequest;
import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final com.zjgsu.ghy.repository.OrderRepository orderRepository;

    public OrderController(OrderService orderService, com.zjgsu.ghy.repository.OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
