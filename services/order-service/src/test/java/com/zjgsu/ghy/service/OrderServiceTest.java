package com.zjgsu.ghy.service;

import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // 注意这里
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockitoBean // 改用 MockitoBean
    private DiscoveryClient discoveryClient;

    @MockitoBean // 改用 MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean // 改用 MockitoBean
    private RestTemplate restTemplate;

    @Test
    void testCreateOrderWhenNoInstancesAvailable() {
        // 模拟获取实例为空
        when(discoveryClient.getInstances(anyString())).thenReturn(Collections.emptyList());

        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            orderService.createOrder(order);
        });

        // 验证返回了 503
        assertEquals(503, exception.getStatusCode().value());
    }
}
