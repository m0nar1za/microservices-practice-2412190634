package com.zjgsu.ghy.controller;
import com.zjgsu.ghy.model.Product;
import com.zjgsu.ghy.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repository;
    public ProductController(ProductRepository repository) { this.repository = repository; }
    @PostMapping public Product create(@RequestBody Product product) { return repository.save(product); }
    @GetMapping("/{id}") public Product get(@PathVariable Long id) { return repository.findById(id).orElseThrow(); }

    @PutMapping("/{id}/deduct")
    public ResponseEntity<?> deductStock(@PathVariable Long id, @RequestParam Integer quantity) {
        Product p = repository.findById(id).orElse(null);
        if (p == null) return ResponseEntity.status(404).body("Product not found");
        if (p.getStock() < quantity) return ResponseEntity.status(409).body("Insufficient stock");
        p.setStock(p.getStock() - quantity);
        repository.save(p);
        return ResponseEntity.ok().build();
    }
}
