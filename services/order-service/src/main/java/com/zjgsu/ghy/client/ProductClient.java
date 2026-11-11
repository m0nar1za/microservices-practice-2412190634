package com.zjgsu.ghy.client;

import com.zjgsu.ghy.dto.ProductDeductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 通过服务名 product-service 调用商品服务。
 * 不写容器 IP；实例选择交给 Nacos Discovery + Spring Cloud LoadBalancer。
 */
@FeignClient(name = "product-service", configuration = ProductFeignConfig.class)
public interface ProductClient {

    @PutMapping("/api/products/{id}/deduct")
    ProductDeductResponse deduct(@PathVariable("id") Long id,
                                 @RequestParam("quantity") Integer quantity);
}
