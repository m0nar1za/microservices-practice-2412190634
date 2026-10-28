package com.zjgsu.ghy.repository;
import com.zjgsu.ghy.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Long> {}
