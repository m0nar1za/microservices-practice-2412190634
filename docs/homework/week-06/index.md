# 作业06：微服务拆分大作业

## 1. 服务拆分设计
详见 `docs/microservices-design.md`。拆分为了 user-service, product-service, order-service 三个独立应用，各自使用独立数据库。订单服务通过 RestTemplate 向商品服务发起 HTTP 调用扣减库存。

## 2. 代码拆分
已在 `services/` 下建立三个独立的 Spring Boot 工程。订单服务通过 RestTemplate 实现了跨服务调用，并包含服务不可用/资源不存在时的异常处理逻辑。

## 3. 本地编排
根目录 `compose.yaml` 包含 3 个微服务和 3 个 MySQL 数据库，使用 `grocery-net` 共享网络，配置了健康检查，一键启动。

## 4. 验证
提供了 `scripts/smoke-test.sh` 脚本验证健康接口。通过 curl 验证了正常下单（扣减库存成功）和库存不足（拦截下单）两个场景。
