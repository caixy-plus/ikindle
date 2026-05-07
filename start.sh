#!/bin/bash

echo "🚀 启动 iKindle 电子书阅读平台..."

# 检查是否安装了必要的工具
check_dependencies() {
    echo "📋 检查依赖..."
    
    # 检查 Java
    if ! command -v java &> /dev/null; then
        echo "❌ 未找到 Java，请先安装 JDK 21"
        exit 1
    fi
    
    # 检查 Node.js
    if ! command -v node &> /dev/null; then
        echo "❌ 未找到 Node.js，请先安装 Node.js 20+"
        exit 1
    fi
    
    # 检查 Maven
    if ! command -v mvn &> /dev/null; then
        echo "❌ 未找到 Maven，请先安装 Maven 3.9+"
        exit 1
    fi
    
    echo "✅ 依赖检查通过"
}

# 启动后端
start_backend() {
    echo "🔧 启动后端服务..."
    cd backend
    
    # 检查是否存在 target 目录
    if [ ! -d "target" ]; then
        echo "📦 编译后端项目..."
        mvn clean compile
    fi
    
    echo "🚀 启动 Spring Boot 应用..."
    mvn spring-boot:run &
    BACKEND_PID=$!
    echo "✅ 后端服务已启动 (PID: $BACKEND_PID)"
    cd ..
}

# 启动前端
start_frontend() {
    echo "🎨 启动前端服务..."
    cd frontend
    
    # 检查 node_modules 是否存在
    if [ ! -d "node_modules" ]; then
        echo "📦 安装前端依赖..."
        npm install
    fi
    
    echo "🚀 启动 React 开发服务器..."
    npm start &
    FRONTEND_PID=$!
    echo "✅ 前端服务已启动 (PID: $FRONTEND_PID)"
    cd ..
}

# 显示服务信息
show_info() {
    echo ""
    echo "🎉 iKindle 电子书阅读平台启动成功！"
    echo ""
    echo "📱 前端地址: http://localhost:3000"
    echo "🔧 后端地址: http://localhost:8080"
    echo "📊 健康检查: http://localhost:8080/api/actuator/health"
    echo ""
    echo "💡 提示:"
    echo "   - 按 Ctrl+C 停止所有服务"
    echo "   - 后端日志会显示在终端中"
    echo "   - 前端修改会自动热重载"
    echo ""
}

# 清理函数
cleanup() {
    echo ""
    echo "🛑 正在停止服务..."
    
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null
        echo "✅ 后端服务已停止"
    fi
    
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null
        echo "✅ 前端服务已停止"
    fi
    
    echo "👋 再见！"
    exit 0
}

# 设置信号处理
trap cleanup SIGINT SIGTERM

# 主函数
main() {
    check_dependencies
    start_backend
    sleep 5  # 等待后端启动
    start_frontend
    sleep 3  # 等待前端启动
    show_info
    
    # 等待用户中断
    wait
}

# 运行主函数
main 