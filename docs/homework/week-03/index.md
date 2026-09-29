# 作业03：Spring Boot 工程创建与运行

## 项目选题与本周计划
沿用第 02 周确定的“社区生鲜团购/配送系统”选题。本周计划创建可运行的 Spring Boot 工程（Java 25 + Spring Boot 4.0.x），配置 application.yml，实现 /api/hello 问候接口与 /actuator/health 健康检查接口，并编写启动测试验证应用上下文加载。

## 项目提案摘要
- 项目名称：社区生鲜团购/配送系统
- 目标用户：居民、团长、供应商、管理员
- 优先业务场景：下单-支付-分拣-核销
- 两个核心模型：User（用户）、Order（订单）

## 工程创建与运行结果
- 已创建 `monolith/` Maven 工程，包含 pom.xml、application.yml、启动类、HelloController。
- 启动命令：`./mvnw spring-boot:run`
- `/api/hello` 响应：`Hello, 社区生鲜团购/配送系统已启动！`
- `/actuator/health` 响应：`{"status":"UP"}`

## 启动测试结果
- 测试命令：`./mvnw test`
- 测试类：`MonolithApplicationTests`
- 结果：BUILD SUCCESS，Spring 应用上下文加载正常。

## 本周完成总结
本周完成了 Spring Boot 单体工程的创建与运行验证。工程位于 `monolith/` 目录，包名符合规范。实现了 HelloController 问候接口与 Actuator 健康检查，编写并运行了 @SpringBootTest 上下文加载测试。根目录 README 已补充完整的运行说明。后续将着手进行业务建模，实现基础的用户与商品管理接口。
