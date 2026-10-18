package com.zjgsu.ghy.service;

import com.zjgsu.ghy.dto.OrderRequest;
import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.model.Product;
import com.zjgsu.ghy.repository.OrderRepository;
import com.zjgsu.ghy.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(OrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 业务规则：库存校验
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("库存不足，下单失败！当前库存：" + product.getStock());
        }

        // 扣减库存
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        // 生成订单
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getPrice() * request.getQuantity());

        return orderRepository.save(order);
    }
}
