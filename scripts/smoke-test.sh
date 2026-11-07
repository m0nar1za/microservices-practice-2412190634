#!/usr/bin/env bash
set -e

echo "=== 检查 Nacos 控制台 HTTP 地址 ==="
# 检查 18080 端口是否正常响应
if curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:18080 | grep -qE "200|302"; then
    echo "Nacos 控制台访问正常 (18080)"
else
    echo "错误: Nacos 控制台不可访问"
    exit 1
fi

echo "=== 检查各服务健康接口 ==="
for port in 8081 8082 8083; do
    echo -n "端口 $port: "
    if curl --fail --silent --show-error "http://localhost:$port/actuator/health" | grep -q '"status":"UP"'; then
        echo "UP"
    else
        echo "DOWN 或无法连接"
        exit 1 # 失败时明确退出非零状态
    fi
done

echo "=== 所有检查通过 ==="
