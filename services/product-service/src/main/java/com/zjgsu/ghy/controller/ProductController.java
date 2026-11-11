package com.zjgsu.ghy.controller;

import com.zjgsu.ghy.dto.ProductDeductResponse;
import com.zjgsu.ghy.model.Product;
import com.zjgsu.ghy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repository;

    @Value("${INSTANCE_ID:product-local}")
    private String instanceId;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return repository.save(product);
    }

    /**
     * 供冒烟/负载均衡脚本直接确认当前实例身份。
     */
    @GetMapping("/instance")
    public Map<String, String> instance() {
        return Map.of("instanceId", instanceId);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PutMapping("/{id}/deduct")
    public ResponseEntity<?> deductStock(@PathVariable Long id, @RequestParam Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest().body("quantity must be positive");
        }
        Product product = repository.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.status(404).body("Product not found");
        }
        if (product.getStock() < quantity) {
            return ResponseEntity.status(409).body("Insufficient stock");
        }
        product.setStock(product.getStock() - quantity);
        repository.save(product);
        return ResponseEntity.ok(new ProductDeductResponse(instanceId, product.getStock()));
    }
}
