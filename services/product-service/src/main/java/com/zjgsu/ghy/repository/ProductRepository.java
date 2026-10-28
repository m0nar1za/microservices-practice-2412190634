package com.zjgsu.ghy.repository;
import com.zjgsu.ghy.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> {}
