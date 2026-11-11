package com.zjgsu.ghy.service;

import com.zjgsu.ghy.client.ProductClient;
import com.zjgsu.ghy.dto.ProductDeductResponse;
import com.zjgsu.ghy.exception.InsufficientStockException;
import com.zjgsu.ghy.exception.ProductNotFoundException;
import com.zjgsu.ghy.exception.ProductServiceUnavailableException;
import com.zjgsu.ghy.model.Order;
import com.zjgsu.ghy.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderSuccess() {
        when(productClient.deduct(1L, 2))
                .thenReturn(new ProductDeductResponse("product-1", 48));
        when(repository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        Order result = orderService.createOrder(order(1L, 2));

        assertEquals(10L, result.getId());
        assertEquals("product-1", result.getProductInstanceId());
        verify(repository).save(any(Order.class));
    }

    @Test
    void createOrderWhenProductNotFound() {
        when(productClient.deduct(1L, 2))
                .thenThrow(new ProductNotFoundException("Product not found"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(order(1L, 2)));

        assertEquals(404, exception.getStatusCode().value());
        verify(repository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWhenStockInsufficient() {
        when(productClient.deduct(1L, 2))
                .thenThrow(new InsufficientStockException("Insufficient stock"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(order(1L, 2)));

        assertEquals(409, exception.getStatusCode().value());
        verify(repository, never()).save(any(Order.class));
    }

    @Test
    void createOrderWhenProductServiceUnavailable() {
        when(productClient.deduct(1L, 2))
                .thenThrow(new ProductServiceUnavailableException("Product service error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(order(1L, 2)));

        assertEquals(503, exception.getStatusCode().value());
        verify(repository, never()).save(any(Order.class));
    }

    private Order order(Long productId, Integer quantity) {
        Order order = new Order();
        order.setUserId(1L);
        order.setProductId(productId);
        order.setQuantity(quantity);
        return order;
    }
}
