package com.zjgsu.ghy.controller;
import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }
    @PostMapping public ResponseEntity<?> create(@RequestBody Order order) {
        try {
            return ResponseEntity.status(201).body(service.createOrder(order));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("unavailable")) return ResponseEntity.status(503).body(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
