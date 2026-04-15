#!/bin/bash

# NOMM 自动编译运行脚本 for Debian 13
# 作者: Cline (AI助手)
# 描述: 自动检查依赖、构建并运行 NOMM (Nuclear Option Mod Manager)

set -e  # 遇到错误时退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 显示帮助信息
show_help() {
    echo "NOMM 自动编译运行脚本"
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help          显示此帮助信息"
    echo "  -r, --run           构建并运行应用程序 (默认)"
    echo "  -b, --build         仅构建应用程序，不运行"
    echo "  -p, --package       构建可执行文件 (.jar)"
    echo "  -c, --clean         清理构建缓存"
    echo "  -d, --deps          仅检查并安装依赖"
    echo "  -v, --verbose       显示详细输出"
    echo ""
    echo "示例:"
    echo "  $0                  # 构建并运行应用程序"
    echo "  $0 --package        # 构建可执行文件"
    echo "  $0 --clean --run    # 清理后构建并运行"
}

# 检查系统是否为 Debian 13
check_debian_version() {
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        if [ "$ID" = "debian" ] && [ "$VERSION_ID" = "13" ]; then
            log_info "检测到 Debian 13 ($VERSION_CODENAME)"
            return 0
        else
            log_warning "检测到 $PRETTY_NAME，本脚本专为 Debian 13 设计，但可能在其他 Debian/Ubuntu 系统上工作"
            return 1
        fi
    else
        log_warning "无法检测操作系统版本，继续执行..."
        return 1
    fi
}

# 检查并安装 Java JDK 21
install_java() {
    log_info "检查 Java JDK 21..."
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
        log_info "已安装 Java 版本: $JAVA_VERSION"
        
        # 检查是否为 JDK 21 或更高版本
        if [[ $JAVA_VERSION == 21* ]] || [[ $JAVA_VERSION == 22* ]] || [[ $JAVA_VERSION == 23* ]]; then
            log_success "Java JDK 21+ 已安装"
            return 0
        else
            log_warning "Java 版本 $JAVA_VERSION 可能不兼容，需要 JDK 21+"
        fi
    else
        log_warning "Java 未安装"
    fi
    
    # 询问是否安装 Java
    read -p "是否安装 OpenJDK 21? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "安装 OpenJDK 21..."
        sudo apt update
        sudo apt install -y openjdk-21-jdk
        
        if [ $? -eq 0 ]; then
            log_success "OpenJDK 21 安装成功"
            
            # 设置 JAVA_HOME
            JAVA_HOME_PATH=$(update-alternatives --list java | head -n 1 | sed 's|/bin/java||')
            if [ -n "$JAVA_HOME_PATH" ]; then
                export JAVA_HOME="$JAVA_HOME_PATH"
                log_info "设置 JAVA_HOME: $JAVA_HOME"
            fi
            
            return 0
        else
            log_error "OpenJDK 21 安装失败"
            return 1
        fi
    else
        log_error "需要 Java JDK 21 才能继续"
        return 1
    fi
}

# 检查其他依赖
check_dependencies() {
    log_info "检查系统依赖..."
    
    local missing_deps=()
    
    # 检查必要的工具
    for cmd in curl unzip; do
        if ! command -v $cmd &> /dev/null; then
            missing_deps+=("$cmd")
        fi
    done
    
    if [ ${#missing_deps[@]} -gt 0 ]; then
        log_warning "缺少依赖: ${missing_deps[*]}"
        read -p "是否安装缺少的依赖? (y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            sudo apt update
            sudo apt install -y "${missing_deps[@]}"
            if [ $? -eq 0 ]; then
                log_success "依赖安装成功"
            else
                log_error "依赖安装失败"
                return 1
            fi
        else
            log_warning "跳过依赖安装，可能会影响构建过程"
        fi
    else
        log_success "所有依赖已安装"
    fi
    
    return 0
}

# 清理构建缓存
clean_build() {
    log_info "清理构建缓存..."
    ./gradlew clean
    if [ $? -eq 0 ]; then
        log_success "构建缓存清理完成"
    else
        log_error "清理构建缓存失败"
        return 1
    fi
}

# 构建应用程序
build_app() {
    log_info "开始构建应用程序..."
    
    if [ "$VERBOSE" = true ]; then
        ./gradlew :composeApp:build --info
    else
        ./gradlew :composeApp:build
    fi
    
    if [ $? -eq 0 ]; then
        log_success "应用程序构建成功"
    else
        log_error "应用程序构建失败"
        return 1
    fi
}

# 运行应用程序
run_app() {
    log_info "启动应用程序..."
    
    if [ "$VERBOSE" = true ]; then
        ./gradlew :composeApp:run --info
    else
        ./gradlew :composeApp:run
    fi
}

# 构建可执行文件
package_app() {
    log_info "构建可执行文件..."
    
    if [ "$VERBOSE" = true ]; then
        ./gradlew :composeApp:packageUberJarForCurrentOS --info
    else
        ./gradlew :composeApp:packageUberJarForCurrentOS
    fi
    
    if [ $? -eq 0 ]; then
        JAR_FILE=$(find composeApp/build/compose/jars -name "*.jar" 2>/dev/null | head -n 1)
        if [ -n "$JAR_FILE" ]; then
            log_success "可执行文件构建完成: $JAR_FILE"
            log_info "运行命令: java -jar \"$JAR_FILE\""
        else
            log_success "可执行文件构建完成，但未找到 JAR 文件"
        fi
    else
        log_error "可执行文件构建失败"
        return 1
    fi
}

# 主函数
main() {
    # 默认选项
    MODE="run"
    CLEAN=false
    VERBOSE=false
    CHECK_DEPS=true
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -r|--run)
                MODE="run"
                shift
                ;;
            -b|--build)
                MODE="build"
                shift
                ;;
            -p|--package)
                MODE="package"
                shift
                ;;
            -c|--clean)
                CLEAN=true
                shift
                ;;
            -d|--deps)
                MODE="deps"
                shift
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            *)
                log_error "未知选项: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # 显示标题
    echo "========================================"
    echo "  NOMM 自动编译运行脚本"
    echo "  Debian 13 专用版本"
    echo "========================================"
    echo ""
    
    # 检查操作系统
    check_debian_version
    
    # 检查并安装依赖
    if [ "$CHECK_DEPS" = true ]; then
        install_java
        if [ $? -ne 0 ]; then
            exit 1
        fi
        
        check_dependencies
        if [ $? -ne 0 ]; then
            exit 1
        fi
    fi
    
    # 仅检查依赖模式
    if [ "$MODE" = "deps" ]; then
        log_success "依赖检查完成"
        exit 0
    fi
    
    # 清理构建缓存
    if [ "$CLEAN" = true ]; then
        clean_build
    fi
    
    # 执行模式
    case "$MODE" in
        "run")
            build_app
            if [ $? -eq 0 ]; then
                echo ""
                log_info "按 Ctrl+C 停止应用程序"
                echo ""
                run_app
            fi
            ;;
        "build")
            build_app
            ;;
        "package")
            build_app
            if [ $? -eq 0 ]; then
                package_app
            fi
            ;;
        *)
            log_error "未知模式: $MODE"
            exit 1
            ;;
    esac
}

# 运行主函数
main "$@"