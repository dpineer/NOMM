# ZIP 整合包导入导出功能测试指南

## 功能概述

项目现已支持 ZIP 格式的模组包导入导出功能。这是对现有 JSON 导入导出的补充。

## 新增功能

### 1. ZIP 导出功能 (`exportModsZip()`)

**位置**: `LocalMods.kt`

**功能说明**:
- 将所有**已启用**的模组打包成一个 ZIP 文件
- ZIP 内容结构:
  ```
  modpack.zip
  ├── modpack.json        (PackageReference 列表)
  └── mods/
      ├── mod_id_1/
      │   ├── [mod文件内容]
      │   └── meta.json    (模组元数据)
      ├── mod_id_2/
      │   ├── [mod文件内容]
      │   └── meta.json
      └── ...
  ```

**工作流程**:
1. 执行 `LocalMods.exportModsZip()`
2. 打开文件保存对话框（默认文件名: `modpack.zip`）
3. 遍历所有已启用的模组
4. 将每个模组文件和 meta.json 添加到 ZIP
5. 将 PackageReference 列表保存为 modpack.json
6. 用户选择保存位置

### 2. ZIP 导入功能 (`importModsZip()`)

**位置**: `LocalMods.kt`

**功能说明**:
- 从 ZIP 文件导入一个完整的模组包
- 自动安装 ZIP 中包含的所有模组文件
- 自动启用/禁用状态管理

**工作流程**:
1. 执行 `LocalMods.importModsZip()`
2. 打开文件选择对话框（选择 `.zip` 文件）
3. 创建临时目录并解压 ZIP 文件
4. 读取 `modpack.json` 获取模组列表
5. 将模组文件从临时目录复制到 `BepInEx/plugins` 目录
6. 调用 `loadInstalledModMetas()` 刷新模组列表
7. 根据 modpack.json 同步启用/禁用状态
8. 清理临时目录

### 3. UI 菜单项

**位置**: `LibraryScreen.kt`

新增菜单项:
- **Export Modpack (ZIP)** / **导出模组包 (ZIP)** - 调用 `exportModsZip()`
- **Import Modpack (ZIP)** / **导入模组包 (ZIP)** - 调用 `importModsZip()`

菜单位置: Library 界面的菜单（更多选项 ⋮ 按钮）

## 修改的文件

1. **LocalMods.kt**
   - 添加 `import java.util.zip.*` 导入
   - 新增 `exportModsZip()` 方法
   - 新增 `importModsZip()` 方法
   - 新增 `addFileToZip()` 私有方法

2. **LibraryScreen.kt**
   - 添加两个新的下拉菜单项
   - 使用 StringResources 中定义的字符串常量

3. **StringResources.kt**
   - 新增 `libraryExportModpackZip()` 方法
   - 新增 `libraryImportModpackZip()` 方法

## 测试步骤

### 测试 ZIP 导出

1. 在应用程序中启用几个模组
2. 点击菜单按钮 ⋮
3. 选择 "Export Modpack (ZIP)"
4. 选择保存位置（如 Downloads 文件夹）
5. 验证生成的 ZIP 文件:
   - 使用 WinRAR、7-Zip 或其他工具打开
   - 检查目录结构是否正确
   - 验证 modpack.json 包含所有已启用模组的 ID 和版本

### 测试 ZIP 导入

1. 禁用所有模组（或清除 BepInEx/plugins 目录）
2. 点击菜单按钮 ⋮
3. 选择 "Import Modpack (ZIP)"
4. 选择之前导出的 ZIP 文件
5. 等待导入完成
6. 验证:
   - 所有模组文件已复制到 BepInEx/plugins
   - 模组启用/禁用状态与导出时一致
   - 模组列表中显示所有导入的模组

### 边界情况测试

1. **空模组包**: 导出一个未启用任何模组的整合包
2. **损坏的 ZIP**: 导入一个格式错误的 ZIP 文件
3. **缺失的 modpack.json**: 导入一个不包含 modpack.json 的 ZIP 文件
4. **重复导入**: 导入同一个整合包两次

## 技术细节

### ZIP 文件处理

- 使用 Java 标准库的 `java.util.zip.ZipOutputStream` 和 `java.util.zip.ZipInputStream`
- 不需要额外的库依赖
- 支持文件夹递归添加到 ZIP

### 文件操作

- 使用 FileKit 库处理文件对话框
- 使用 Kotlin 标准库的 `File` 类处理文件操作
- 使用 `copyRecursively()` 进行文件夹复制

### 错误处理

- 所有异常都被捕获并打印到控制台
- 使用 try-catch 包装文件操作
- 临时目录在导入完成后自动清理

## 已知限制

1. 错误处理目前仅打印到控制台，没有用户通知
2. 大文件 ZIP 的导出可能需要较长时间
3. 不支持增量备份（每次都是完整的模组包）

## 未来改进建议

1. 添加进度条显示导出/导入进度
2. 添加用户通知（成功/失败提示）
3. 支持分差备份
4. 添加密码保护选项
5. 支持自定义 ZIP 结构和元数据

## 代码位置快速索引

| 函数/类 | 文件 | 行号 |
|--------|------|------|
| `exportModsZip()` | LocalMods.kt | 128-168 |
| `importModsZip()` | LocalMods.kt | 170-231 |
| `addFileToZip()` | LocalMods.kt | 233-243 |
| UI 菜单项 | LibraryScreen.kt | 95-134 |
| 字符串资源 | StringResources.kt | 103-104 |
