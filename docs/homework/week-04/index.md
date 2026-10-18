# 作业04：单体应用与数据库持久化

## 业务建模
- 核心流程：居民提交订单 -> 校验库存 -> 扣减库存 -> 生成订单。
- 实体：商品（Product）与订单（Order），关系为多对一。
- 业务规则：下单时校验库存，库存不足抛出异常。

## 单体应用实现
- 实现了 `Product` 和 `Order` 双实体建模。
- 包含 `ProductController` 和 `OrderController`，共 4 个 REST 接口。
- Service 层实现了严格的库存校验规则，超卖时抛出异常。

## 数据库持久化
- 已在本地 WSL 安装 MySQL 并创建 `grocery_db`。
- 配置了 `application.yml` 的 DataSource，使用 JPA 自动建表。
- 数据库表结构详见 `docs/database.md`。

## 测试与验证
- `./mvnw clean test` 执行成功（Tests run: 3, Failures: 0），验证了业务规则。
- 接口验证成功，商品和订单数据正常写入 MySQL 数据库。
