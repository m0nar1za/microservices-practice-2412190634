package com.zjgsu.ghy.service;

import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.repository.OrderRepository;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public OrderService(OrderRepository repository, RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.repository = repository; 
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    @Transactional
    public Order createOrder(Order order) {
        // 1. 动态获取商品服务实例
        List<ServiceInstance> instances = discoveryClient.getInstances("product-service");
        
        // 2. 列表为空返回 503
        if (instances == null || instances.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Product service unavailable");
        }

        // 3. 使用实例 URI 发送请求
        ServiceInstance instance = instances.get(0);
        String url = instance.getUri() + "/api/products/" + order.getProductId() + "/deduct?quantity=" + order.getQuantity();

        try {
            restTemplate.put(url, null);
        } catch (HttpClientErrorException e) {
            // 4. 保留原有 404 业务响应
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            }
            if (e.getStatusCode().value() == 409) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Remote error");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Connection failed");
        }

        return repository.save(order);
    }
}
