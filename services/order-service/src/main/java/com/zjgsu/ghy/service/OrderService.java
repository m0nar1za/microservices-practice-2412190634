package com.zjgsu.ghy.service;
import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
@Service
public class OrderService {
    private final OrderRepository repository;
    private final RestTemplate restTemplate;
    @Value("${services.product-url}") private String productUrl;
    public OrderService(OrderRepository repository, RestTemplate restTemplate) {
        this.repository = repository; this.restTemplate = restTemplate;
    }
    @Transactional
    public Order createOrder(Order order) {
        try {
            restTemplate.put(productUrl + "/api/products/" + order.getProductId() + "/deduct?quantity=" + order.getQuantity(), null);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) throw new RuntimeException("Product not found");
            if (e.getStatusCode().value() == 409) throw new RuntimeException("Insufficient stock");
            throw new RuntimeException("Remote error");
        } catch (Exception e) {
            throw new RuntimeException("Product service unavailable");
        }
        return repository.save(order);
    }
}
