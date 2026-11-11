#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [ ! -f .env ]; then
  echo "错误：找不到 .env，请先执行 bash scripts/init-env.sh" >&2
  exit 1
fi

set -a
# shellcheck disable=SC1091
source .env
set +a

ORDER_BASE="http://localhost:8083"
PRODUCT_BASE="http://localhost:8082"
PRODUCT2_BASE="http://localhost:8092"

INSTANCES_FILE=""

cleanup() {
  if [ -n "${INSTANCES_FILE}" ] && [ -f "${INSTANCES_FILE}" ]; then
    rm -f "${INSTANCES_FILE}"
  fi
  docker compose start product-service product-service-2 >/dev/null 2>&1 || true
}
trap cleanup EXIT

wait_http() {
  local url="$1"
  local name="$2"
  local i
  for i in $(seq 1 45); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "  OK   $name"
      return 0
    fi
    sleep 2
  done
  echo "  FAIL $name ($url) 不可用" >&2
  return 1
}

order_count() {
  docker compose exec -T order-db mysql \
    -uroot -p"${MYSQL_ROOT_PASSWORD}" order_db -N \
    -e "select count(*) from orders;" | tr -d '[:space:]'
}

echo "== 0. 等待服务就绪 =="
wait_http "$PRODUCT_BASE/actuator/health" "product-service (8082)"
wait_http "$PRODUCT2_BASE/actuator/health" "product-service-2 (8092)"
wait_http "$ORDER_BASE/actuator/health" "order-service (8083)"

echo
echo "== 1. 创建测试商品（库存 100） =="
PRODUCT_JSON=$(curl -fsS -X POST "$PRODUCT_BASE/api/products" \
  -H 'Content-Type: application/json' \
  -d '{"name":"e2e-loadbalance","price":1.0,"stock":100}')
PRODUCT_ID=$(printf '%s' "$PRODUCT_JSON" | sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p')
if [ -z "$PRODUCT_ID" ]; then
  echo "无法解析商品 id：$PRODUCT_JSON" >&2
  exit 1
fi
echo "  productId=${PRODUCT_ID}"

echo
echo "== 2. 负载均衡：20 次下单，期望命中 product-1 和 product-2 =="
INSTANCES_FILE=$(mktemp)
for _ in $(seq 1 20); do
  BODY=$(curl -fsS -X POST "$ORDER_BASE/api/orders" \
    -H 'Content-Type: application/json' \
    -d "{\"userId\":1,\"productId\":${PRODUCT_ID},\"quantity\":1}")
  printf '%s\n' "$BODY" | sed -n 's/.*"productInstanceId":"\([^"]*\)".*/\1/p' >> "$INSTANCES_FILE"
done
sort -u "$INSTANCES_FILE" -o "$INSTANCES_FILE"
echo "  命中实例："
sed 's/^/    - /' "$INSTANCES_FILE"
INSTANCE_COUNT=$(wc -l < "$INSTANCES_FILE" | tr -d '[:space:]')
if [ "$INSTANCE_COUNT" -ne 2 ]; then
  echo "  FAIL 期望命中 2 个实例，实际 ${INSTANCE_COUNT} 个" >&2
  exit 1
fi
echo "  PASS 两个 product-service 实例都被调用"

echo
echo "== 3. 资源不存在：期望 404 =="
CODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$ORDER_BASE/api/orders" \
  -H 'Content-Type: application/json' \
  -d '{"userId":1,"productId":999999,"quantity":1}')
if [ "$CODE" != "404" ]; then
  echo "  FAIL 期望 404，实际 ${CODE}" >&2
  exit 1
fi
echo "  PASS 404"

echo
echo "== 4. 库存不足：期望 409 =="
LOW_JSON=$(curl -fsS -X POST "$PRODUCT_BASE/api/products" \
  -H 'Content-Type: application/json' \
  -d '{"name":"e2e-low-stock","price":1.0,"stock":1}')
LOW_ID=$(printf '%s' "$LOW_JSON" | sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p')
CODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$ORDER_BASE/api/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"userId\":1,\"productId\":${LOW_ID},\"quantity\":2}")
if [ "$CODE" != "409" ]; then
  echo "  FAIL 期望 409，实际 ${CODE}" >&2
  exit 1
fi
echo "  PASS 409"

echo
echo "== 5. 依赖不可用：期望 503，且不新增订单 =="
BEFORE=$(order_count)
docker compose stop product-service product-service-2 >/dev/null
sleep 15
CODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$ORDER_BASE/api/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"userId\":1,\"productId\":${PRODUCT_ID},\"quantity\":1}")
AFTER=$(order_count)
if [ "$CODE" != "503" ]; then
  echo "  FAIL 期望 503，实际 ${CODE}" >&2
  exit 1
fi
if [ "$BEFORE" != "$AFTER" ]; then
  echo "  FAIL 依赖不可用时订单数变化：before=${BEFORE}, after=${AFTER}" >&2
  exit 1
fi
echo "  PASS 503，订单数保持 ${BEFORE}"

echo
echo "== 6. 恢复商品服务：期望重新返回 201 =="
docker compose start product-service product-service-2 >/dev/null
wait_http "$PRODUCT_BASE/actuator/health" "product-service (8082)"
wait_http "$PRODUCT2_BASE/actuator/health" "product-service-2 (8092)"

CODE=""
for _ in $(seq 1 15); do
  CODE=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$ORDER_BASE/api/orders" \
    -H 'Content-Type: application/json' \
    -d "{\"userId\":1,\"productId\":${PRODUCT_ID},\"quantity\":1}")
  if [ "$CODE" = "201" ]; then
    break
  fi
  sleep 2
done
if [ "$CODE" != "201" ]; then
  echo "  FAIL 恢复后期望 201，实际 ${CODE}" >&2
  exit 1
fi
echo "  PASS 恢复 201"

echo
echo "== ALL PASS：服务通信、错误处理、负载均衡、依赖故障验证完成 =="
