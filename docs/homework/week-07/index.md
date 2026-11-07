# 作业07：服务注册与发现

## 1. 部署 Nacos 并注册服务

- **Nacos 部署**：在根目录 `compose.yaml` 中引入了 `nacos/nacos-server:v3.2.4`。配置了鉴权 Token 和 Identity，映射了控制台端口 `18080`、服务端端口 `8848` 和 SDK 端口 `9848`。配置了 `nacos-data` 数据卷。
- **服务注册**：三个微服务（user-service、product-service、order-service）均通过 `application.yml` 配置了 Nacos 的 Discovery，并统一注册到 `PERSONAL_GROUP` 分组。
- **注册结果**：在 Nacos 控制台查看，三个服务实例均成功注册，实例的 IP 与映射端口均正确显示，且状态为健康。

## 2. 配置隔离并完成服务发现调用

- **调用方向**：`order-service`（订单服务）下单时调用 `product-service`（商品服务）扣减库存。
- **请求示例**：
  `POST http://localhost:8083/api/orders`，请求体 `{"userId":1,"productId":2,"quantity":2}`
- **实现逻辑**：在 `OrderService` 中移除了固定地址，注入 `DiscoveryClient`。每次调用时通过 `discoveryClient.getInstances("product-service")` 获取实例列表。
- **异常处理**：
  - 获取实例列表为空时，抛出异常并返回 `503 SERVICE_UNAVAILABLE`。
  - 保留原有的 404（商品不存在）和 409（库存不足）业务响应。
- **测试验证**：编写了 `OrderServiceTest`，通过 `@MockitoBean` 模拟 `DiscoveryClient` 返回空列表，验证了 503 的异常抛出逻辑。
- **实际结果**：正常启动状态下发起下单，接口成功返回 `HTTP 201`，并生成了订单记录。

## 3. 验证健康状态、下线与恢复

- **正常状态**：依赖正常时，下单成功（201），库存数据正确扣减。
- **服务下线**：执行 `docker compose stop product-service` 后，Nacos 控制台中的 product-service 实例消失。再次通过订单服务发起请求，接口返回 `503 SERVICE_UNAVAILABLE` 及明确的提示信息，未产生无效的订单记录。
- **服务恢复**：执行 `docker compose start product-service`，等待实例重新注册后，再次发起请求，接口恢复返回 `201` 成功。
- **健康状态区别**：Actuator 健康检查反映的是应用内部组件（如数据库连接）的状态；而 Nacos 实例存活反映的是 SDK 与服务端的心跳连接状态。数据库故障可能导致 Actuator 返回 DOWN，但实例仍在 Nacos 列表中，二者默认不会自动联动。

## 4. 验证脚本与测试

- **验证脚本**：更新了根目录下的 `scripts/smoke-test.sh`。脚本包含对 Nacos 控制台（18080）的检查，以及对三个微服务健康接口的检查。如果任一检查失败，脚本会执行 `exit 1` 返回非零状态。
- **执行结果**：在全部服务正常时，脚本输出“所有检查通过”；在手动停止 `user-service` 后执行，脚本能准确定位 8081 端口失效，并返回退出代码 1。
- **测试边界**：各微服务的测试（`./mvnw test`）已关闭真实的 Nacos 注册，并使用 H2 内存数据库，保证测试不依赖外部环境即可运行。

*(注：相关的运行与测试截图均已保存至 `docs/homework/week-07/screenshots/` 目录下)*
