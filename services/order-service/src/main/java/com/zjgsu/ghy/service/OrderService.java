package com.zjgsu.ghy.service;

import com.zjgsu.ghy.client.ProductClient;
import com.zjgsu.ghy.dto.ProductDeductResponse;
import com.zjgsu.ghy.exception.InsufficientStockException;
import com.zjgsu.ghy.exception.ProductNotFoundException;
import com.zjgsu.ghy.exception.ProductServiceUnavailableException;
import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.repository.OrderRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;

    public OrderService(OrderRepository repository, ProductClient productClient) {
        this.repository = repository;
        this.productClient = productClient;
    }

    @Transactional
    public Order createOrder(Order order) {
        if (order.getProductId() == null || order.getQuantity() == null || order.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "productId and positive quantity are required");
        }

        try {
            // OpenFeign 按服务名调用；LoadBalancer 在多个 product-service 实例间选择
            ProductDeductResponse response = productClient.deduct(
                    order.getProductId(), order.getQuantity());
            // 记录本次实际命中的商品服务实例，便于验证负载均衡
            order.setProductInstanceId(response.getInstanceId());
        } catch (ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        } catch (InsufficientStockException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        } catch (ProductServiceUnavailableException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Product service unavailable");
        } catch (FeignException e) {
            // 连接失败、超时、未分类的远程错误
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Product service unavailable");
        }

        return repository.save(order);
    }
}
