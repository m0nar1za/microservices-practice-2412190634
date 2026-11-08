package com.zjgsu.ghy.controller;

import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Order order) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(order));
        } catch (ResponseStatusException e) {
            // 404 / 409 / 503 由 Service 抛出，这里原样透传
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (RuntimeException e) {
            // 未预期的本地异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
