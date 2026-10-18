package com.zjgsu.ghy.service;

import com.zjgsu.ghy.dto.OrderRequest;
import com.zjgsu.ghy.model.Product;
import com.zjgsu.ghy.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testCreateOrderSuccess() {
        Product product = new Product();
        product.setName("测试生鲜-土豆");
        product.setPrice(5.0);
        product.setStock(100);
        product = productRepository.save(product);

        OrderRequest request = new OrderRequest();
        request.setProductId(product.getId());
        request.setQuantity(10);

        var order = orderService.createOrder(request);
        assertNotNull(order.getId());
        assertEquals(50.0, order.getTotalPrice());
        
        // 验证库存是否扣减
        Product updatedProduct = productRepository.findById(product.getId()).get();
        assertEquals(90, updatedProduct.getStock());
    }

    @Test
    void testCreateOrderInsufficientStock() {
        Product product = new Product();
        product.setName("测试生鲜-苹果");
        product.setPrice(10.0);
        product.setStock(5);
        product = productRepository.save(product);

        OrderRequest request = new OrderRequest();
        request.setProductId(product.getId());
        request.setQuantity(10); // 买 10 个，但只有 5 个

        // 验证业务规则是否抛出异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(request);
        });
        assertTrue(exception.getMessage().contains("库存不足"));
    }
}
