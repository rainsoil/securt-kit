@echo off
echo ========================================
echo SecurtKit 测试项目启动脚本
echo ========================================

echo 正在编译项目...
call mvn clean compile

if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！

echo.
echo 选择运行模式：
echo 1. 运行所有测试
echo 2. 运行用户服务测试
echo 3. 运行订单服务测试
echo 4. 运行集成测试
echo 5. 启动应用服务器
echo 6. 退出

set /p choice=请输入选择 (1-6): 

if "%choice%"=="1" (
    echo 运行所有测试...
    call mvn test
) else if "%choice%"=="2" (
    echo 运行用户服务测试...
    call mvn test -Dtest=UserServiceTest
) else if "%choice%"=="3" (
    echo 运行订单服务测试...
    call mvn test -Dtest=OrderServiceTest
) else if "%choice%"=="4" (
    echo 运行集成测试...
    call mvn test -Dtest=IntegrationTest
) else if "%choice%"=="5" (
    echo 启动应用服务器...
    call mvn spring-boot:run
) else if "%choice%"=="6" (
    echo 退出
    exit /b 0
) else (
    echo 无效选择！
    pause
    exit /b 1
)

echo.
echo 操作完成！
pause
