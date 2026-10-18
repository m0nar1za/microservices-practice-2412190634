# 数据库设计

## 表结构说明
系统当前包含两张核心业务表：`products`（商品表）和 `orders`（订单表）。

### 1. products 表
- `id` (BIGINT, 主键, 自增)：商品唯一标识。
- `name` (VARCHAR, 非空)：商品名称。
- `price` (DOUBLE, 非空)：商品单价。
- `stock` (INT, 非空)：当前库存数量。

### 2. orders 表
- `id` (BIGINT, 主键, 自增)：订单唯一标识。
- `product_id` (BIGINT, 外键, 非空)：关联的商品 ID。
- `quantity` (INT, 非空)：购买数量。
- `total_price` (DOUBLE, 非空)：订单总金额。
- `create_time` (DATETIME)：下单时间。

## 关系与约束
- `orders.product_id` 外键关联 `products.id`，构成多对一关系。
- 下单时，通过 Service 层校验库存，保证 `quantity <= stock`。

## 本地 MySQL 配置
- 在 WSL Ubuntu 中安装 MySQL，创建数据库 `grocery_db`。
- 配置账号密码均为 `root`，JPA 设置 `ddl-auto: update` 自动建表。
