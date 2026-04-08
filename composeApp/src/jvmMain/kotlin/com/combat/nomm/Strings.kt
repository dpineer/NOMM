package com.combat.nomm

object Strings {
    // App
    const val app_name = "Nuclear Option Mod Manager"
    const val app_version = "3.0.0"
    const val app_by = "by Combat"
    
    // Navigation
    const val nav_discover = "Discover"
    const val nav_library = "Library"
    const val nav_settings = "Settings"
    
    // Settings Screen
    const val settings_about = "About"
    const val settings_path_configuration = "Path Configuration"
    const val settings_manifest = "Manifest"
    const val settings_appearance = "Appearance"
    const val settings_folders = "Folders"
    const val settings_system = "System"         // 新增：系统信息分组标题
    
    // About Section
    const val about_nomm = "Nuclear Option Mod Manager"
    const val about_version = "Version"
    const val about_github = "GitHub"
    const val about_github_url = "github.com/Combat787/NOMM"
    
    // Path Configuration
    const val path_game_folder = "Game Folder"
    const val path_not_found = "Not Found"
    const val path_select_nuclear_option_folder = "Select Nuclear Option Folder"
    
    // Manifest
    const val manifest_source_url = "Manifest Source URL"
    const val manifest_fake = "Fake Manifest"
    const val manifest_fake_description = "Generates Fake Manifest Data useful to test the UI better."
    
    // Appearance
    const val appearance_theme_accent = "Theme Accent"
    const val appearance_theme_brightness = "Theme Brightness"
    const val appearance_theme_style = "Theme Style"
    const val appearance_theme_contrast = "Theme Contrast"
    const val appearance_language = "Interface Language"   // 新增
    
    // Folders
    const val folders_open_nuclear_option_folder = "Open Nuclear Option Folder"
    const val folders_open_nuclear_option_folder_desc = "Click to open the Folder containing the Logs, Missions and Blocklist."
    const val folders_open_game_folder = "Open Nuclear Option Game Folder"
    const val folders_open_game_folder_desc = "Click to open the Folder containing the Game Files and BepInEx."
    
    // Mod Detail Screen
    const val mod_details = "Details"
    const val mod_versions = "Versions"
    const val mod_close = "Close"
    const val mod_update = "Update"
    const val mod_install = "Install"
    const val mod_dependencies = "Dependencies"
    
    // Dependency Sections
    const val deps_extends = "Extends"
    const val deps_dependencies = "Dependencies"
    const val deps_incompatibilities = "Incompatibilities"
    const val deps_dependents = "Dependents"
    const val deps_none = "None"
    const val deps_missing = "MISSING"
    
    // Artifact
    const val artifact_hash = "Hash:"
    const val artifact_not_found = "Artifact version not found."
    const val artifact_na = "N/A"
    
    // Library Screen
    const val library_export_modpack = "Export Modpack"
    const val library_import_modpack = "Import Modpack"
    const val library_add_from_file = "Add from file"
    const val library_options = "Options"
    const val library_nothing_here = "Nothing here. huh"
    
    // Search Screen
    const val search_mods = "Search mods..."
    const val search_clear = "Clear"
    
    // BepInEx
    const val bepinex_install = "Install\nBepInEx"
    
    // Dialog
    const val dialog_close = "Close"
    const val dialog_copy_details = "Copy Details"
    const val dialog_bepinex_linux_instructions = "To make BepInEx work on Linux you need to add the following to the Steam Launch Options for Nuclear Option."
    const val dialog_bepinex_linux_command = "WINEDLLOVERRIDES=\"winhttp=n,b\" %command%"
    
    // Theme Names
    const val theme_light = "Light"
    const val theme_dark = "Dark"
    const val theme_system = "System"
    
    // Palette Style Names
    const val palette_tonal_spot = "Tonal Spot"
    const val palette_neutral = "Neutral"
    const val palette_vibrant = "Vibrant"
    const val palette_expressive = "Expressive"
    const val palette_rainbow = "Rainbow"
    const val palette_fruit_salad = "Fruit Salad"
    const val palette_monochrome = "Monochrome"
    const val palette_fidelity = "Fidelity"
    const val palette_content = "Content"
    
    // Contrast Names
    const val contrast_default = "Default"
    const val contrast_medium = "Medium"
    const val contrast_high = "High"
    const val contrast_reduced = "Reduced"

    // OS / System info（新增）
    const val system_os_label = "Operating System"
    const val system_os_windows = "Windows"
    const val system_os_linux = "Linux"
    const val system_os_macos = "macOS"
    const val system_os_unknown = "Unknown"

    // Language（新增，用于中文 UI 回退，目前仅用于选项显示）
    const val language_english = "English"
    const val language_chinese = "中文"
}
