package com.combat.nomm

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

object StringResources {
    // ── 非 Composable 常量（供非 Composable 上下文使用）────────────────
    val appNameConstant = "Nuclear Option Mod Manager"
    val appVersionConstant = "3.0.0"
    val appByConstant = "by Combat"

    // ── 工具方法：根据当前语言配置返回对应字符串 ─────────────────────────
    // 参数 en：英文字符串；参数 zh：中文字符串
    // 调用方需在 @Composable 上下文中读取 SettingsManager.config.value.language
    private fun lang() = SettingsManager.config.value.language

    private fun t(en: String, zh: String): String =
        if (lang() == AppLanguage.ZH) zh else en

    // ── App ──────────────────────────────────────────────────────────
    @Composable fun appName() = t("Nuclear Option Mod Manager", "Nuclear Option 模组管理器")
    @Composable fun appVersion() = "3.0.0"
    @Composable fun appBy() = t("by Combat", "作者：Combat")

    // ── Navigation ───────────────────────────────────────────────────
    @Composable fun navDiscover() = t("Discover", "发现")
    @Composable fun navLibrary() = t("Library", "库")
    @Composable fun navSettings() = t("Settings", "设置")

    // ── Settings Screen ──────────────────────────────────────────────
    @Composable fun settingsAbout() = t("About", "关于")
    @Composable fun settingsPathConfiguration() = t("Path Configuration", "路径配置")
    @Composable fun settingsManifest() = t("Manifest", "清单")
    @Composable fun settingsAppearance() = t("Appearance", "外观")
    @Composable fun settingsFolders() = t("Folders", "文件夹")
    @Composable fun settingsSystem() = t("System", "系统信息")   // 新增

    // ── About ────────────────────────────────────────────────────────
    @Composable fun aboutNomm() = "Nuclear Option Mod Manager"
    @Composable fun aboutVersion() = t("Version", "版本")
    @Composable fun aboutGithub() = "GitHub"
    @Composable fun aboutGithubUrl() = "github.com/Combat787/NOMM"
    @Composable fun aboutBranchUrl() = "github.com/dpineer/NOMM"
    @Composable fun aboutMaintainer() = t("Maintained by", "当前分支维护者")

    // ── Path ─────────────────────────────────────────────────────────
    @Composable fun pathGameFolder() = t("Game Folder", "游戏目录")
    @Composable fun pathNotFound() = t("Not Found", "未找到")
    @Composable fun pathSelectNuclearOptionFolder() = t("Select Nuclear Option Folder", "选择 Nuclear Option 游戏目录")

    // ── Manifest ─────────────────────────────────────────────────────
    @Composable fun manifestSourceUrl() = t("Manifest Source URL", "清单来源 URL")
    @Composable fun manifestFake() = t("Fake Manifest", "虚假清单")
    @Composable fun manifestFakeDescription() = t(
        "Generates Fake Manifest Data useful to test the UI better.",
        "生成虚假清单数据，用于 UI 测试。"
    )

    // ── Appearance ───────────────────────────────────────────────────
    @Composable fun appearanceThemeAccent() = t("Theme Accent", "主题色调")
    @Composable fun appearanceThemeBrightness() = t("Theme Brightness", "主题亮度")
    @Composable fun appearanceThemeStyle() = t("Theme Style", "主题风格")
    @Composable fun appearanceThemeContrast() = t("Theme Contrast", "主题对比度")
    @Composable fun appearanceLanguage() = t("Interface Language", "界面语言")   // 新增

    // ── Folders ──────────────────────────────────────────────────────
    @Composable fun foldersOpenNuclearOptionFolder() = t("Open Nuclear Option Folder", "打开 Nuclear Option 数据目录")
    @Composable fun foldersOpenNuclearOptionFolderDesc() = t(
        "Click to open the Folder containing the Logs, Missions and Blocklist.",
        "点击打开包含日志、任务和黑名单的目录。"
    )
    @Composable fun foldersOpenGameFolder() = t("Open Nuclear Option Game Folder", "打开游戏安装目录")
    @Composable fun foldersOpenGameFolderDesc() = t(
        "Click to open the Folder containing the Game Files and BepInEx.",
        "点击打开包含游戏文件和 BepInEx 的目录。"
    )

    // ── Mod Detail ───────────────────────────────────────────────────
    @Composable fun modDetails() = t("Details", "详情")
    @Composable fun modVersions() = t("Versions", "版本")
    @Composable fun modClose() = t("Close", "关闭")
    @Composable fun modUpdate() = t("Update", "更新")
    @Composable fun modInstall() = t("Install", "安装")
    @Composable fun modDependencies() = t("Dependencies", "依赖")

    // ── Dependency Sections ──────────────────────────────────────────
    @Composable fun depsExtends() = t("Extends", "继承自")
    @Composable fun depsDependencies() = t("Dependencies", "依赖项")
    @Composable fun depsIncompatibilities() = t("Incompatibilities", "不兼容项")
    @Composable fun depsDependents() = t("Dependents", "被依赖项")
    @Composable fun depsNone() = t("None", "无")
    @Composable fun depsMissing() = "MISSING"

    // ── Artifact ─────────────────────────────────────────────────────
    @Composable fun artifactHash() = t("Hash:", "哈希值：")
    @Composable fun artifactNotFound() = t("Artifact version not found.", "未找到对应版本。")
    @Composable fun artifactNa() = "N/A"

    // ── Library Screen ───────────────────────────────────────────────
    @Composable fun libraryExportModpack() = t("Export Modpack", "导出模组包")
    @Composable fun libraryImportModpack() = t("Import Modpack", "导入模组包")
    @Composable fun libraryAddFromFile() = t("Add from file", "从文件添加")
    @Composable fun libraryOptions() = t("Options", "选项")
    @Composable fun libraryNothingHere() = t("Nothing here. huh", "这里什么都没有。")

    // ── Search Screen ────────────────────────────────────────────────
    @Composable fun searchMods() = t("Search mods...", "搜索模组...")
    @Composable fun searchClear() = t("Clear", "清除")

    // ── BepInEx ──────────────────────────────────────────────────────
    @Composable fun bepInExInstall() = t("Install\nBepInEx", "安装\nBepInEx")

    // ── Dialog ───────────────────────────────────────────────────────
    @Composable fun dialogClose() = t("Close", "关闭")
    @Composable fun dialogCopyDetails() = t("Copy Details", "复制详情")
    @Composable fun dialogBepInExLinuxInstructions() = t(
        "To make BepInEx work on Linux you need to add the following to the Steam Launch Options for Nuclear Option.",
        "在 Linux 上运行 BepInEx，需将以下内容添加到 Nuclear Option 的 Steam 启动选项中。"
    )
    @Composable fun dialogBepInExLinuxCommand() = "WINEDLLOVERRIDES=\"winhttp=n,b\" %command%"

    // ── Theme Names ──────────────────────────────────────────────────
    @Composable fun themeLight() = t("Light", "亮色")
    @Composable fun themeDark() = t("Dark", "暗色")
    @Composable fun themeSystem() = t("System", "跟随系统")

    // ── Palette Style Names ──────────────────────────────────────────
    @Composable fun paletteTonalSpot() = t("Tonal Spot", "色调点")
    @Composable fun paletteNeutral() = t("Neutral", "中性")
    @Composable fun paletteVibrant() = t("Vibrant", "鲜艳")
    @Composable fun paletteExpressive() = t("Expressive", "表现力")
    @Composable fun paletteRainbow() = t("Rainbow", "彩虹")
    @Composable fun paletteFruitSalad() = t("Fruit Salad", "水果沙拉")
    @Composable fun paletteMonochrome() = t("Monochrome", "单色")
    @Composable fun paletteFidelity() = t("Fidelity", "保真")
    @Composable fun paletteContent() = t("Content", "内容")

    // ── Contrast Names ───────────────────────────────────────────────
    @Composable fun contrastDefault() = t("Default", "默认")
    @Composable fun contrastMedium() = t("Medium", "中等")
    @Composable fun contrastHigh() = t("High", "高")
    @Composable fun contrastReduced() = t("Reduced", "降低")

    // ── System / OS（新增）──────────────────────────────────────────
    @Composable fun systemOsLabel() = t("Operating System", "操作系统")
    fun systemOsWindows() = "Windows"
    fun systemOsLinux() = "Linux"
    fun systemOsMacos() = "macOS"
    @Composable fun systemOsUnknown() = t("Unknown", "未知")

    // ── Helper ───────────────────────────────────────────────────────
    @Composable
    fun artifactHashWithValue(hash: String?): AnnotatedString {
        val hashText = hash ?: artifactNa()
        return buildAnnotatedString {
            append(artifactHash())
            append(" ")
            append(hashText)
        }
    }
}