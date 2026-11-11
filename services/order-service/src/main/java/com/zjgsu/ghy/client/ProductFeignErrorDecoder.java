package com.zjgsu.ghy.client;

import com.zjgsu.ghy.exception.InsufficientStockException;
import com.zjgsu.ghy.exception.ProductNotFoundException;
import com.zjgsu.ghy.exception.ProductServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * 把商品服务的 HTTP 错误转换成业务异常，便于 OrderService 统一映射。
 */
public class ProductFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        if (status == 404) {
            return new ProductNotFoundException("Product not found");
        }
        if (status == 409) {
            return new InsufficientStockException("Insufficient stock");
        }
        if (status >= 500) {
            return new ProductServiceUnavailableException("Product service error");
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
