package com.zjgsu.ghy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long productId;
    private Integer quantity;

    /**
     * 不落库，只用于把本次实际命中的商品服务实例返回给调用方，
     * 便于 e2e-test.sh 验证负载均衡。
     */
    @Transient
    private String productInstanceId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getProductInstanceId() { return productInstanceId; }
    public void setProductInstanceId(String productInstanceId) { this.productInstanceId = productInstanceId; }
}
