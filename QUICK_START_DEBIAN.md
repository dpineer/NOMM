# NOMM 在 Debian 13 上的快速开始指南

本指南介绍如何在 Debian 13 系统上快速构建和运行 NOMM (Nuclear Option Mod Manager)。

## 自动编译运行脚本

我们提供了一个自动化的 Bash 脚本 `build-and-run.sh`，它可以自动处理依赖检查、构建和运行过程。

### 脚本功能

- ✅ 自动检测 Debian 13 系统
- ✅ 检查并安装 Java JDK 21+
- ✅ 检查系统依赖 (curl, unzip 等)
- ✅ 构建应用程序
- ✅ 运行应用程序
- ✅ 构建可执行文件 (.jar)
- ✅ 清理构建缓存
- ✅ 彩色日志输出

### 使用方法

#### 1. 给脚本添加执行权限
```bash
chmod +x build-and-run.sh
```

#### 2. 查看帮助信息
```bash
./build-and-run.sh --help
```

#### 3. 检查系统依赖
```bash
./build-and-run.sh --deps
```

#### 4. 构建并运行应用程序 (默认)
```bash
./build-and-run.sh
```

#### 5. 仅构建应用程序
```bash
./build-and-run.sh --build
```

#### 6. 构建可执行文件 (.jar)
```bash
./build-and-run.sh --package
```

#### 7. 清理后构建并运行
```bash
./build-and-run.sh --clean --run
```

#### 8. 显示详细输出
```bash
./build-and-run.sh --verbose
```

### 手动构建方法

如果你更喜欢手动构建，可以按照以下步骤：

#### 1. 安装 Java JDK 21
```bash
sudo apt update
sudo apt install -y openjdk-21-jdk
```

#### 2. 验证 Java 安装
```bash
java --version
```

#### 3. 构建并运行应用程序
```bash
./gradlew :composeApp:run
```

#### 4. 构建可执行文件
```bash
./gradlew :composeApp:packageUberJarForCurrentOS
```

构建完成后，可执行文件位于：
```
composeApp/build/compose/jars/
```

运行可执行文件：
```bash
java -jar composeApp/build/compose/jars/NOMM-*.jar
```

### 系统要求

- **操作系统**: Debian 13 (Trixie) 或兼容的 Linux 发行版
- **Java**: JDK 21 或更高版本
- **内存**: 至少 3GB RAM (用于 Gradle 构建)
- **磁盘空间**: 至少 2GB 可用空间

### 故障排除

#### 问题1: Java 版本不兼容
**症状**: 构建失败，提示需要 Java 21+
**解决方案**: 
```bash
sudo apt install -y openjdk-21-jdk
sudo update-alternatives --config java  # 选择 Java 21
```

#### 问题2: 权限不足
**症状**: 无法执行脚本或创建文件
**解决方案**:
```bash
chmod +x build-and-run.sh
chmod +x gradlew
```

#### 问题3: 网络问题导致依赖下载失败
**症状**: Gradle 构建时下载超时
**解决方案**:
- 检查网络连接
- 使用代理 (如果需要):
  ```bash
  export HTTP_PROXY=http://your-proxy:port
  export HTTPS_PROXY=http://your-proxy:port
  ```

#### 问题4: 内存不足
**症状**: 构建过程中 Gradle 崩溃
**解决方案**:
- 增加 Gradle 内存限制:
  ```bash
  export GRADLE_OPTS="-Xmx4g"
  ./build-and-run.sh
  ```

### 脚本内部工作原理

1. **系统检测**: 检查是否为 Debian 13
2. **依赖检查**: 验证 Java JDK 21+ 和其他必要工具
3. **构建过程**: 使用 Gradle 构建项目
4. **运行/打包**: 根据选项运行应用程序或打包为 JAR

### 高级用法

#### 自定义 Java 版本
如果你安装了多个 Java 版本，可以指定使用哪个版本：
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./build-and-run.sh
```

#### 跳过依赖检查
如果你确定所有依赖都已安装，可以修改脚本跳过检查（不推荐）。

#### 构建特定版本
脚本使用项目默认配置，要构建特定版本需要修改 `composeApp/build.gradle.kts` 中的版本号。

### 贡献

如果你发现脚本有问题或想添加新功能，欢迎提交 Issue 或 Pull Request。

### 许可证

本脚本遵循 MIT 许可证，与 NOMM 项目相同。

---

**注意**: NOMM 是一个第三方工具，与 Nuclear Option 官方无关。使用模组可能影响游戏稳定性，请定期备份游戏存档。