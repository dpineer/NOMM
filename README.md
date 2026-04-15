# NOMM - Nuclear Option Mod Manager

一个为游戏 Nuclear Option 设计的模组管理器

![NOMM Logo](icons/iconpng.png)

## 功能特性

### 核心功能
- **自动 BepInEx 安装** - 自动检测并安装 BepInEx 框架
- **模组搜索与发现** - 浏览和搜索社区模组
- **模组安装与更新** - 一键安装、更新模组
- **依赖关系管理** - 自动解析模组依赖关系
- **冲突检测** - 检测模组之间的不兼容性
- **本地模组管理** - 从文件添加本地模组
- **模组启用/禁用** - 轻松切换模组状态
- **模组卸载** - 完全移除不需要的模组

### 用户界面
- **多语言支持** - 支持英文和中文界面
- **可定制主题** - 多种主题颜色和风格选择
- **现代化设计** - 基于 Material Design 3 的现代化界面
- **响应式布局** - 适配不同屏幕尺寸

### 技术特性
- **离线缓存** - 模组信息本地缓存
- **自动更新检查** - 定期检查模组更新
- **错误恢复** - 安装失败时的自动恢复机制
- **日志记录** - 详细的操作日志

## 系统要求

### 最低配置
- **操作系统**: Windows 10/11, Linux (Ubuntu 20.04+), macOS 11+
- **Java**: JDK 17 或更高版本
- **游戏**: Nuclear Option (Steam 版本)
- **磁盘空间**: 至少 500MB 可用空间

### 推荐配置
- **内存**: 8GB RAM 或更多
- **显卡**: 支持 OpenGL 3.3+
- **网络**: 稳定的互联网连接（用于模组下载）

## 安装与使用

### 快速开始

1. **下载 NOMM**
   - 从 [GitHub Releases](https://github.com/Combat787/NOMM/releases) 下载最新版本
   - 或者从源代码构建（见下文）

2. **首次运行**
   - 启动 NOMM 应用程序
   - 设置 Nuclear Option 游戏目录
   - NOMM 会自动检测并安装 BepInEx（如果需要）

3. **浏览和安装模组**
   - 在"发现"页面浏览可用模组
   - 点击模组查看详细信息
   - 点击"安装"按钮安装模组

### 从源代码构建

#### 环境要求
- JDK 17 或更高版本
- Gradle 8.14.3 或更高版本

#### 构建步骤

**在 macOS/Linux 上：**
```bash
# 克隆仓库
git clone https://github.com/Combat787/NOMM.git
cd NOMM

# 构建并运行
./gradlew :composeApp:run
```

**在 Windows 上：**
```cmd
# 克隆仓库
git clone https://github.com/Combat787/NOMM.git
cd NOMM

# 构建并运行
.\gradlew.bat :composeApp:run
```

#### 构建发布版本
```bash
# 构建可执行文件
./gradlew :composeApp:packageUberJarForCurrentOS

# 输出文件位于：composeApp/build/compose/jars/
```

### Debian 13 自动编译运行脚本
对于 Debian 13 用户，我们提供了一个自动化的构建脚本，可以自动检查依赖、构建和运行应用程序：

```bash
# 1. 给脚本添加执行权限
chmod +x build-and-run.sh

# 2. 查看帮助信息
./build-and-run.sh --help

# 3. 构建并运行应用程序 (默认)
./build-and-run.sh

# 4. 构建可执行文件
./build-and-run.sh --package

# 5. 仅检查依赖
./build-and-run.sh --deps
```

脚本功能包括：
- 自动检测 Debian 13 系统
- 检查并安装 Java JDK 21+
- 检查系统依赖
- 构建应用程序
- 运行应用程序或构建可执行文件
- 清理构建缓存

详细使用说明请查看 [QUICK_START_DEBIAN.md](QUICK_START_DEBIAN.md)。

## 界面语言设置

NOMM 支持多语言界面：

1. 点击左侧导航栏的"设置"图标
2. 在"外观"部分找到"界面语言"选项
3. 选择"中文"或"English"
4. 界面将立即切换语言

当前支持的语言：
- **English** (默认)
- **中文** (简体中文)

## 项目结构

```
NOMM/
├── composeApp/              # 主应用程序模块
│   ├── src/jvmMain/kotlin/com/combat/nomm/
│   │   ├── App.kt          # 应用程序入口点
│   │   ├── SettingsScreen.kt      # 设置界面
│   │   ├── ModDetailScreen.kt     # 模组详情界面
│   │   ├── ModsListScreen.kt      # 模组列表界面
│   │   ├── MainNavigationRail.kt  # 主导航栏
│   │   ├── StringResources.kt     # 多语言字符串资源
│   │   └── Strings.kt             # 字符串常量
│   └── composeResources/   # 资源文件（图标、字体等）
├── shared/                 # 共享代码模块
├── server/                 # 服务器模块（可选）
└── gradle/                 # Gradle 配置
```

## 多语言支持架构

NOMM 使用基于 Compose 的多语言支持系统：

### 字符串资源管理
- **Strings.kt** - 包含硬编码的英文字符串常量
- **StringResources.kt** - 包含可组合函数，根据当前语言设置返回英文或中文字符串

### 添加新语言支持
1. 在 `StringResources.kt` 中添加新的字符串函数
2. 在 `SettingsScreen.kt` 的语言选择器中添加新语言选项
3. 在 `AppLanguage` 枚举中添加新语言

### 示例代码
```kotlin
// 在 StringResources.kt 中添加新字符串
@Composable fun exampleString() = t("Example", "示例")

// 在代码中使用
Text(text = StringResources.exampleString())
```

## 常见问题

### Q: NOMM 无法找到我的游戏目录
A: 请手动指定 Nuclear Option 的安装目录。通常位于：
- **Windows**: `C:\Program Files (x86)\Steam\steamapps\common\Nuclear Option`
- **Linux**: `~/.steam/steam/steamapps/common/Nuclear Option`
- **macOS**: `~/Library/Application Support/Steam/steamapps/common/Nuclear Option`

### Q: 模组安装失败
A: 请检查：
1. 网络连接是否正常
2. 磁盘空间是否充足
3. 防病毒软件是否阻止了文件操作
4. 游戏目录是否有写入权限

### Q: 如何手动添加本地模组？
A: 在"库"页面点击"从文件添加"按钮，选择本地的 `.dll` 模组文件。

### Q: 如何切换界面语言？
A: 在设置页面的"外观"部分，找到"界面语言"选项进行切换。

## 开发指南

### 代码风格
- 使用 Kotlin 官方编码规范
- Compose 函数使用 PascalCase 命名
- 变量和函数使用 camelCase 命名
- 常量使用 UPPER_SNAKE_CASE 命名

### 提交贡献
1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 测试
```bash
# 运行单元测试
./gradlew test

# 运行集成测试
./gradlew :composeApp:run
```

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 致谢

- **游戏开发者** - Nuclear Option 开发团队
- **模组社区** - 所有为 Nuclear Option 创建模组的贡献者
- **图标设计** - Shumatsu
- **开源项目** - 所有使用的开源库的维护者

## 支持与反馈

- **问题报告**: [GitHub Issues](https://github.com/Combat787/NOMM/issues)
- **功能请求**: 通过 Issues 提交
- **讨论**: [GitHub Discussions](https://github.com/Combat787/NOMM/discussions)

---

**注意**: NOMM 是一个第三方工具，与 Nuclear Option 官方无关。使用模组可能影响游戏稳定性，请定期备份游戏存档。