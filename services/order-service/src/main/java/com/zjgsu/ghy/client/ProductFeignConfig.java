package com.zjgsu.ghy.client;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ProductFeignConfig {

    @Bean
    public ErrorDecoder productErrorDecoder() {
        return new ProductFeignErrorDecoder();
    }
}
