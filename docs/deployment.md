# iKindle 部署指南

## 环境要求

### 系统要求
- 操作系统: Linux, macOS, Windows
- 内存: 最少 4GB RAM
- 存储: 最少 10GB 可用空间

### 软件依赖
- **Java**: JDK 21+ (推荐 OpenJDK 21)
- **Node.js**: 20.11.1+ (推荐 LTS 版本)
- **Maven**: 3.9.6+
- **PostgreSQL**: 16.2+
- **Redis**: 7.2.4+

## 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd ikindle
```

### 2. 配置数据库
```bash
# 创建数据库
createdb ikindle

# 或者使用 psql
psql -U postgres
CREATE DATABASE ikindle;
```

### 3. 配置 Redis
```bash
# 启动 Redis 服务
redis-server

# 或者使用 Docker
docker run -d -p 6379:6379 redis:7.2.4
```

### 4. 修改配置
编辑 `backend/src/main/resources/application.yml` 文件，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ikindle
    username: your-username
    password: your-password
```

### 5. 启动项目
```bash
# 使用启动脚本（推荐）
chmod +x start.sh
./start.sh

# 或者手动启动
# 后端
cd backend
mvn spring-boot:run

# 前端（新终端）
cd frontend
npm install
npm start
```

## 生产环境部署

### 1. 后端部署

#### 使用 Docker
```bash
# 构建镜像
cd backend
docker build -t ikindle-backend .

# 运行容器
docker run -d \
  --name ikindle-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=your-db-host \
  -e DB_PORT=5432 \
  -e DB_NAME=ikindle \
  -e DB_USER=your-username \
  -e DB_PASSWORD=your-password \
  ikindle-backend
```

#### 使用 JAR 包
```bash
# 构建 JAR 包
cd backend
mvn clean package -DskipTests

# 运行
java -jar target/ikindle-backend-1.0.0.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

### 2. 前端部署

#### 构建生产版本
```bash
cd frontend
npm run build
```

#### 使用 Nginx 部署
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/frontend/build;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### 使用 Docker
```bash
# 构建镜像
cd frontend
docker build -t ikindle-frontend .

# 运行容器
docker run -d \
  --name ikindle-frontend \
  -p 80:80 \
  ikindle-frontend
```

## 环境变量配置

### 后端环境变量
```bash
# 数据库配置
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ikindle
DB_USER=postgres
DB_PASSWORD=password

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT 配置
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=86400000

# 阿里云 OSS 配置
OSS_ENDPOINT=https://oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=your-access-key-id
OSS_ACCESS_KEY_SECRET=your-access-key-secret
OSS_BUCKET_NAME=ikindle-books
```

### 前端环境变量
```bash
# API 地址
REACT_APP_API_URL=http://localhost:8080/api

# 应用名称
REACT_APP_NAME=iKindle

# 版本号
REACT_APP_VERSION=1.0.0
```

## 监控和日志

### 健康检查
```bash
# 检查后端健康状态
curl http://localhost:8080/api/actuator/health

# 检查前端状态
curl http://localhost:3000
```

### 日志配置
后端日志默认输出到控制台，生产环境建议配置日志文件：

```yaml
logging:
  file:
    name: logs/ikindle-backend.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 性能监控
```bash
# 查看 JVM 状态
jstat -gc <pid>

# 查看内存使用
jmap -heap <pid>

# 查看线程状态
jstack <pid>
```

## 备份和恢复

### 数据库备份
```bash
# 备份数据库
pg_dump -U postgres ikindle > ikindle_backup.sql

# 恢复数据库
psql -U postgres ikindle < ikindle_backup.sql
```

### 文件备份
```bash
# 备份上传的文件
tar -czf uploads_backup.tar.gz /path/to/uploads/

# 恢复文件
tar -xzf uploads_backup.tar.gz -C /path/to/restore/
```

## 故障排除

### 常见问题

1. **端口被占用**
   ```bash
   # 查看端口占用
   lsof -i :8080
   lsof -i :3000
   
   # 杀死进程
   kill -9 <pid>
   ```

2. **数据库连接失败**
   - 检查 PostgreSQL 服务是否启动
   - 验证数据库连接信息
   - 检查防火墙设置

3. **Redis 连接失败**
   - 检查 Redis 服务是否启动
   - 验证 Redis 配置
   - 检查网络连接

4. **前端无法访问后端**
   - 检查 CORS 配置
   - 验证 API 地址
   - 检查网络连接

### 日志查看
```bash
# 查看后端日志
tail -f logs/ikindle-backend.log

# 查看前端日志
tail -f frontend/logs/npm-debug.log
```

## 安全建议

1. **数据库安全**
   - 使用强密码
   - 限制数据库访问IP
   - 定期备份数据

2. **应用安全**
   - 使用 HTTPS
   - 配置防火墙
   - 定期更新依赖

3. **文件安全**
   - 限制文件上传类型
   - 配置文件访问权限
   - 使用 CDN 加速

## 性能优化

1. **数据库优化**
   - 创建适当的索引
   - 优化查询语句
   - 配置连接池

2. **应用优化**
   - 启用缓存
   - 配置 CDN
   - 压缩静态资源

3. **服务器优化**
   - 调整 JVM 参数
   - 配置负载均衡
   - 监控系统资源

## 更新部署

### 后端更新
```bash
# 停止服务
pkill -f ikindle-backend

# 备份当前版本
cp ikindle-backend.jar ikindle-backend.jar.backup

# 部署新版本
cp new-version.jar ikindle-backend.jar

# 启动服务
java -jar ikindle-backend.jar
```

### 前端更新
```bash
# 构建新版本
npm run build

# 备份当前版本
cp -r build build.backup

# 部署新版本
rm -rf build
npm run build
```

## 联系支持

如果遇到问题，请：
1. 查看日志文件
2. 检查配置是否正确
3. 参考故障排除部分
4. 提交 Issue 到项目仓库 