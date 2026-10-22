# 作业05：大作业实践 Docker 容器化

## 1. Dockerfile 与镜像构建
在 `monolith/` 下编写了多阶段构建 `Dockerfile`，使用 Maven 镜像构建，JRE 镜像运行，并添加了 `.dockerignore` 排除无关文件。
执行 `docker build -t grocery-monolith:week05 ./monolith` 成功（由 Compose 自动完成）。

## 2. Compose 编排与启动
在仓库根目录创建了 `compose.yaml`，编排了 `app` 和 `mysql:8.4.11`。配置了端口映射、共享网络、健康检查，并通过 `.env` 注入密码环境变量。
执行 `docker compose up -d --build`，`docker compose ps` 显示两个服务均正常运行。

## 3. 个人业务 API 验证
容器启动后，通过 `curl` 验证了 `/actuator/health` 返回 UP，并成功调用了 `POST /api/products` 创建商品，以及 `GET /api/products` 查询商品。

## 4. 数据持久化验证
使用 `POST` 接口创建商品后，执行 `docker compose down` 删除容器，再执行 `docker compose up -d` 重建。再次查询数据库，商品数据依然存在，证明命名数据卷 `mysql-data` 成功持久化数据。
