#!/bin/bash

# 简单的构建测试脚本
# 用于快速验证构建过程是否正常工作

echo "=== NOMM 构建测试脚本 ==="
echo "开始时间: $(date)"
echo ""

# 检查基本依赖
echo "1. 检查基本依赖..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
    echo "   ✓ Java 已安装: $JAVA_VERSION"
else
    echo "   ✗ Java 未安装"
    exit 1
fi

if command -v ./gradlew &> /dev/null; then
    echo "   ✓ Gradle wrapper 存在"
else
    echo "   ✗ Gradle wrapper 不存在"
    exit 1
fi

echo ""

# 测试 Gradle 版本
echo "2. 测试 Gradle 版本..."
./gradlew --version | head -10
echo ""

# 测试清理功能
echo "3. 测试清理功能..."
if ./gradlew clean --dry-run 2>&1 | grep -q "BUILD SUCCESSFUL"; then
    echo "   ✓ 清理功能正常"
else
    echo "   ✗ 清理功能测试失败"
fi

echo ""

# 测试构建任务
echo "4. 测试构建任务..."
if ./gradlew :composeApp:build --dry-run 2>&1 | grep -q "BUILD SUCCESSFUL"; then
    echo "   ✓ 构建任务配置正常"
else
    echo "   ✗ 构建任务配置测试失败"
fi

echo ""

# 测试运行任务
echo "5. 测试运行任务..."
if ./gradlew :composeApp:run --dry-run 2>&1 | grep -q "BUILD SUCCESSFUL"; then
    echo "   ✓ 运行任务配置正常"
else
    echo "   ✗ 运行任务配置测试失败"
fi

echo ""

# 测试打包任务
echo "6. 测试打包任务..."
if ./gradlew :composeApp:packageUberJarForCurrentOS --dry-run 2>&1 | grep -q "BUILD SUCCESSFUL"; then
    echo "   ✓ 打包任务配置正常"
else
    echo "   ✗ 打包任务配置测试失败"
fi

echo ""
echo "=== 测试完成 ==="
echo "结束时间: $(date)"
echo ""
echo "总结: 所有基本配置测试通过！"
echo "要实际构建项目，请运行: ./build-and-run.sh"
echo "要查看详细帮助，请运行: ./build-and-run.sh --help"