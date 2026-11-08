# 数据库设计

## 1. 单体版（monolith/，第 4 周）

系统包含两张核心业务表：`products`（商品表）和 `orders`（订单表）。

### products 表
- `id` (BIGINT, 主键, 自增)：商品唯一标识。
- `name` (VARCHAR, 非空)：商品名称。
- `price` (DOUBLE, 非空)：商品单价。
- `stock` (INT, 非空)：当前库存数量。

### orders 表
- `id` (BIGINT, 主键, 自增)：订单唯一标识。
- `product_id` (BIGINT, 外键, 非空)：关联的商品 ID。
- `quantity` (INT, 非空)：购买数量。
- `total_price` (DOUBLE, 非空)：订单总金额。
- `create_time` (DATETIME)：下单时间。

### 关系与约束
- `orders.product_id` 外键关联 `products.id`，构成多对一关系。
- 下单时由 Service 层校验 `quantity <= stock`。
- 本地 MySQL 数据库：`grocery_db`，JPA 使用 `ddl-auto: update` 自动建表。

## 2. 微服务版（services/，第 6–7 周）

每个服务拥有独立数据库，不建立跨库外键。

| 服务 | 数据库 | 表 | 主要字段 |
|---|---|---|---|
| user-service | user_db | users | id, username |
| product-service | product_db | products | id, name, price, stock |
| order-service | order_db | orders | id, user_id, product_id, quantity |

### 关系与约束
- `orders.user_id` 逻辑引用 `user_db.users.id`，不建跨库外键。
- `orders.product_id` 逻辑引用 `product_db.products.id`，不建跨库外键。
- 库存校验在 product-service 的扣减接口中完成，order-service 通过 HTTP 调用。
- 当前课程演示使用 root 账号连接各库；生产环境应为每个库创建独立账号并遵循最小权限。
