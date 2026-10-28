#!/usr/bin/env bash
set -euo pipefail
echo "=== 开始健康检查 ==="
for port in 8081 8082 8083; do
  echo -n "端口 $port: "
  curl --fail --silent --show-error "http://localhost:$port/actuator/health" | grep -q '"status":"UP"' && echo "UP"
done
echo "=== 完成 ==="
