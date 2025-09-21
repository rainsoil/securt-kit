#!/bin/bash

echo "========================================"
echo "SecurtKit 测试项目启动脚本"
echo "========================================"

echo "正在编译项目..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo "编译成功！"

echo ""
echo "选择运行模式："
echo "1. 运行所有测试"
echo "2. 运行用户服务测试"
echo "3. 运行订单服务测试"
echo "4. 运行集成测试"
echo "5. 启动应用服务器"
echo "6. 退出"

read -p "请输入选择 (1-6): " choice

case $choice in
    1)
        echo "运行所有测试..."
        mvn test
        ;;
    2)
        echo "运行用户服务测试..."
        mvn test -Dtest=UserServiceTest
        ;;
    3)
        echo "运行订单服务测试..."
        mvn test -Dtest=OrderServiceTest
        ;;
    4)
        echo "运行集成测试..."
        mvn test -Dtest=IntegrationTest
        ;;
    5)
        echo "启动应用服务器..."
        mvn spring-boot:run
        ;;
    6)
        echo "退出"
        exit 0
        ;;
    *)
        echo "无效选择！"
        exit 1
        ;;
esac

echo ""
echo "操作完成！"
