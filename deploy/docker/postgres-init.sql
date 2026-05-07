-- iKindle PostgreSQL 初始化脚本
-- 创建数据库 + 默认用户(若未存在)

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'ikindle') THEN
    CREATE DATABASE ikindle ENCODING 'UTF8' TEMPLATE template0 LC_COLLATE 'C' LC_CTYPE 'C';
  END IF;
END
$$;

-- 时区设置
ALTER DATABASE ikindle SET timezone TO 'Asia/Shanghai';
