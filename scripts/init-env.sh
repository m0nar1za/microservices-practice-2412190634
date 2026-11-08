#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if [ -f .env ]; then
  echo "错误: .env 已存在。请先备份并删除，再运行本脚本。" >&2
  exit 1
fi

DB_PASSWORD="$(openssl rand -hex 16)"
NACOS_TOKEN="$(openssl rand -base64 32)"
NACOS_IDENTITY_VALUE="$(openssl rand -hex 16)"

cat > .env <<EOFX
MYSQL_ROOT_PASSWORD=$DB_PASSWORD
USER_DB_PASSWORD=$DB_PASSWORD
PRODUCT_DB_PASSWORD=$DB_PASSWORD
ORDER_DB_PASSWORD=$DB_PASSWORD

NACOS_AUTH_TOKEN=$NACOS_TOKEN
NACOS_AUTH_IDENTITY_KEY=courseServerIdentity
NACOS_AUTH_IDENTITY_VALUE=$NACOS_IDENTITY_VALUE
NACOS_PASSWORD=nacos
EOFX

chmod 600 .env

echo "已生成 .env (权限 600)。"
echo "Nacos 控制台默认账号: nacos / nacos"
echo "如果你在 Nacos 控制台修改了密码，请同步修改 .env 的 NACOS_PASSWORD。"
