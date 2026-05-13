# iKindle - 电子书阅读平台

iKindle 是一个功能完整的电子书阅读平台，支持电子书管理、多端阅读、书架管理、订单支付与数据同步。

![iKindle 截图](screenshot.png)

## 技术架构

| 组件 | 技术栈 |
|---|---|
| 后端 | Java 21 + Spring Boot 3.2.3 + PostgreSQL + Redis + QueryDSL |
| PC 前端 | React 18 + TypeScript + Ant Design |
| 移动端 | Flutter（iOS / Android / macOS / Windows） |
| 管理后台 | Node.js + React |
| 认证 | JWT Token |

## 快速开始

### 环境要求
- Java 21+, PostgreSQL 16+, Redis 7.2+

### 启动后端
```bash
cd backend
mvn compile
mvn spring-boot:run
```

> 项目采用 MIT 协议开源，由 Cai Xin Yun 开发。
