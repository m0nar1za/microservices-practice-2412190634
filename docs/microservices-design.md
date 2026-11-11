# 生鲜配送系统微服务拆分设计

## 1. 拆分映射与职责
- **user-service (端口 8081)**：负责用户（买家、团长）的信息管理。独立数据库 `user_db`，表 `users`。
- **product-service (端口 8082)**：负责生鲜商品的信息与库存管理。独立数据库 `product_db`，表 `products`。
- **order-service (端口 8083)**：负责订单的创建与流转。独立数据库 `order_db`，表 `orders`。订单中仅保存 `productId` 和 `userId`，不建立外键。

## 2. 服务调用关系
- `order-service` 在创建订单时，通过 OpenFeign 按服务名 `product-service` 调用商品服务的扣减库存接口；Nacos 提供实例列表，Spring Cloud LoadBalancer 在两个 product-service 实例间轮询。扣减成功才保存订单；失败或商品不存在则拒绝下单。

## 3. 架构图
```mermaid
graph TD
    Client[客户端] -->|HTTP 8081| User[user-service]
    Client -->|HTTP 8082| Product[product-service]
    Client -->|HTTP 8083| Order[order-service]

    Order -->|OpenFeign PUT /api/products/{id}/deduct| Product

    User --> UserDB[(user_db)]
    Product --> ProductDB[(product_db)]
    Order --> OrderDB[(order_db)]
