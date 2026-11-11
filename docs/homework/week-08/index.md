# 作业08：服务通信与负载均衡

## 1. 选择一个跨服务业务流程

**流程**：居民下单 → order-service 通过 OpenFeign 调用 product-service 扣减库存 → 扣减成功后保存订单。

- 调用方：`order-service`（8083）
- 被调用方：`product-service`（两个实例，容器内 8082；宿主机 8082 / 8092）
- 接口路径：`PUT /api/products/{id}/deduct?quantity={quantity}`
- 设计说明：详见 [`docs/service-communication.md`](../../service-communication.md)

**实现思路**：

- 订单服务不再使用 RestTemplate + DiscoveryClient 手动拼 URL，而是声明 `ProductClient`，通过 `@FeignClient(name = "product-service")` 调用。
- 商品服务返回 `{instanceId, remainingStock}`，订单响应带上 `productInstanceId`，用于证明实际命中了哪个实例。
- 失败分类：404 商品不存在、409 库存不足、503 依赖不可用；失败时不保存订单。

**运行命令**：

```bash
docker compose up -d --build
bash scripts/e2e-test.sh
```

**结果**：

- 20 次下单命中 `product-1` 和 `product-2` 两个实例。
- 商品不存在返回 404，库存不足返回 409。
- 停掉两个 product-service 后下单返回 503，订单表数量不变。
- 恢复后再次下单返回 201。

**截图**：`docs/homework/week-08/screenshots/`（下单 201、Nacos 两个实例、负载均衡结果）。

## 2. 使用 OpenFeign

**实现思路**：

- `services/order-service/pom.xml` 增加 `spring-cloud-starter-openfeign` 和 `spring-cloud-starter-loadbalancer`。
- `OrderServiceApplication` 增加 `@EnableFeignClients`。
- 新增 `ProductClient`：

```java
@FeignClient(name = "product-service", configuration = ProductFeignConfig.class)
public interface ProductClient {
    @PutMapping("/api/products/{id}/deduct")
    ProductDeductResponse deduct(@PathVariable("id") Long id,
                                 @RequestParam("quantity") Integer quantity);
}
```

- `ProductFeignErrorDecoder` 把商品服务的 404/409/5xx 转换为业务异常，`OrderService` 再映射为 404/409/503。
- Feign 超时：`connectTimeout: 2000`、`readTimeout: 3000`。

**运行命令**：

```bash
cd services/order-service
./mvnw test
```

**结果**：

- `OrderServiceTest` 四个用例通过：成功、404、409、503。
- 真实调用时，订单响应包含 `productInstanceId`，说明 OpenFeign 确实调用了商品服务。

**截图**：`docs/homework/week-08/screenshots/`（OpenFeign 代码、测试通过、下单 201 响应）。

## 3. 负载均衡验证

**实现思路**：

- `compose.yaml` 中增加 `product-service-2`，`INSTANCE_ID=product-2`，宿主机端口 8092；原 `product-service` 使用 `INSTANCE_ID=product-1`，端口 8082。
- 两个实例容器内都是 8082，注册到 Nacos 的同一个服务名 `product-service`。
- `ProductController` 的扣库存响应返回 `instanceId`；订单服务把它写入 `Order.productInstanceId`（`@Transient`，不落库），订单响应里可见。
- `e2e-test.sh` 连续下单 20 次，收集 `productInstanceId` 去重，断言同时出现 `product-1` 和 `product-2`。

**运行命令**：

```bash
bash scripts/e2e-test.sh
```

**结果**：

```text
命中实例：
    - product-1
    - product-2
PASS 两个 product-service 实例都被调用
```

**说明**：不是只看 Nacos 注册列表；是调用方 order-service 实际返回的 `productInstanceId` 证明两个实例都被访问。

**截图**：`docs/homework/week-08/screenshots/`（`docker compose ps` 两个 product 实例、20 次下单结果、两个实例身份接口）。

## 4. 测试

**实现思路**：

- 单元测试：`OrderServiceTest` 用 Mockito 模拟 `ProductClient`，覆盖成功、404、409、503，不依赖真实 Nacos/MySQL。
- 端到端测试：`scripts/e2e-test.sh` 从仓库根目录运行，覆盖健康检查、负载均衡、404、409、503、恢复 201。
- 依赖不可用：脚本停掉两个 product-service，断言下单返回 503，并对比 order-db 的订单数量，确认没有新增订单。
- 写操作超时：Feign 读取超时 3 秒；如果商品服务已扣库存但响应超时，订单服务会按 503 处理，最终结果可能未知，需要幂等/对账，不能当成成功。

**运行命令**：

```bash
bash scripts/e2e-test.sh
```

**结果**：

```text
PASS 两个 product-service 实例都被调用
PASS 404
PASS 409
PASS 503，订单数保持 N
PASS 恢复 201
== ALL PASS ==
```

**截图**：`docs/homework/week-08/screenshots/`（e2e 脚本完整输出、503 时订单数不变、恢复 201）。

## 5. 提交文件

- `services/order-service/src/main/java/com/zjgsu/ghy/client/ProductClient.java`
- `services/order-service/src/main/java/com/zjgsu/ghy/client/ProductFeignConfig.java`
- `services/order-service/src/main/java/com/zjgsu/ghy/client/ProductFeignErrorDecoder.java`
- `services/order-service/src/main/java/com/zjgsu/ghy/exception/*`
- `services/order-service/src/main/java/com/zjgsu/ghy/dto/ProductDeductResponse.java`
- `services/order-service/src/main/java/com/zjgsu/ghy/service/OrderService.java`
- `services/order-service/src/test/java/com/zjgsu/ghy/service/OrderServiceTest.java`
- `services/product-service/src/main/java/com/zjgsu/ghy/controller/ProductController.java`
- `services/product-service/src/main/java/com/zjgsu/ghy/dto/ProductDeductResponse.java`
- `compose.yaml`
- `scripts/e2e-test.sh`
- `docs/service-communication.md`
- `docs/homework/week-08/index.md`
- `docs/homework/week-08/screenshots/`（截图）
