package com.combat.nomm

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.materialkolor.Contrast
import com.materialkolor.PaletteStyle
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

// ── 工具函数：获取当前操作系统名称 ──────────────────────────────────────
// 置于文件顶层，供 SettingsScreen 内调用
fun getCurrentOsName(): String {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> StringResources.systemOsWindows()
        os.contains("mac") -> StringResources.systemOsMacos()
        os.contains("nix") || os.contains("nux") || os.contains("aix") -> StringResources.systemOsLinux()
        else -> "Unknown" // 硬编码，因为 systemOsUnknown() 是 @Composable
    }
}

@Composable
fun SettingsScreen() {
    val currentConfig by SettingsManager.config
    val uriHandler = LocalUriHandler.current
    val state = rememberScrollState()

    val isScrollable by remember {
        derivedStateOf { state.maxValue > 0 }
    }
    
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(1f).verticalScroll(state),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            val scope = rememberCoroutineScope()
            SettingsGroup(title = StringResources.settingsAbout()) {
                SettingsInfoRow(
                    infoName = StringResources.aboutNomm(),
                    infoData = StringResources.appBy()
                )
                SettingsInfoRow(
                    infoName = StringResources.aboutVersion(),
                    infoData = StringResources.appVersion()
                )
                ClickableSettingsRow(
                    StringResources.aboutGithub(),
                    StringResources.aboutGithubUrl(),
                    onClick = {
                        scope.launch {
                            uriHandler.openUri("https://github.com/Combat787/NOMM")
                        }
                    }
                )
            }
            
            SettingsGroup(title = StringResources.settingsPathConfiguration()) {
                ClickableSettingsRow(
                    label = StringResources.pathGameFolder(), 
                    subLabel = currentConfig.gamePath ?: StringResources.pathNotFound(), 
                    onClick = {
                        scope.launch {
                            val directory = FileKit.openDirectoryPicker(
                                title = "Select Nuclear Option Folder" // 硬编码英文，因为 StringResources.pathSelectNuclearOptionFolder() 是 @Composable
                            )
                            directory?.file?.path?.let { path ->
                                val exeFile = File(path, "NuclearOption.exe")
                                if (exeFile.exists()) SettingsManager.updateConfig(
                                    currentConfig.copy(
                                        gamePath = path
                                    )
                                )
                            }
                        }
                    })
            }
            SettingsGroup(title = StringResources.settingsManifest()) {
                SettingsTextFieldRow(
                    label = StringResources.manifestSourceUrl(),
                    value = currentConfig.manifestUrl,
                    onValueChange = {
                        SettingsManager.updateConfig(currentConfig.copy(manifestUrl = it))
                        RepoMods.fetchManifest()
                    },
                    placeholder = "",
                )
                SettingsSwitchRow(
                    label = StringResources.manifestFake(),
                    subLabel = StringResources.manifestFakeDescription(),
                    checked = currentConfig.fakeManifest,
                    onCheckedChange = { newValue ->
                        SettingsManager.updateConfig(currentConfig.copy(fakeManifest = newValue))
                        RepoMods.fetchManifest()
                    })
            }
            SettingsGroup(title = StringResources.settingsAppearance()) {
                SettingsColorPicker(
                    label = StringResources.appearanceThemeAccent(), 
                    selectedHue = currentConfig.hueValue, 
                    onHueSelected = { newHue ->
                        SettingsManager.updateConfig(
                            currentConfig.copy(hueValue = newHue)
                        )
                    })
                SettingsDropdownRow(
                    label = StringResources.appearanceThemeBrightness(),
                    subLabel = currentConfig.theme.getStringName(),
                    options = Theme.entries.associateBy { theme -> theme.getStringName() },
                    onOptionSelected = {
                        SettingsManager.updateConfig(currentConfig.copy(theme = it))
                    })
                SettingsDropdownRow(
                    label = StringResources.appearanceThemeStyle(),
                    subLabel = currentConfig.paletteStyle.getStringName(),
                    options = listOf(
                        PaletteStyle.TonalSpot, PaletteStyle.Neutral, PaletteStyle.Vibrant, PaletteStyle.Expressive
                    ).associateBy { theme -> theme.getStringName() },
                    onOptionSelected = {
                        SettingsManager.updateConfig(currentConfig.copy(paletteStyle = it))
                    })
                SettingsDropdownRow(
                    label = StringResources.appearanceThemeContrast(),
                    subLabel = currentConfig.contrast.getStringName(),
                    options = Contrast.entries.associateBy { contrast -> contrast.getStringName() },
                    onOptionSelected = {
                        SettingsManager.updateConfig(currentConfig.copy(contrast = it))
                    })
                // ── 新增：界面语言选择 ─────────────────────────────────────────────
                SettingsDropdownRow(
                    label = StringResources.appearanceLanguage(),
                    subLabel = currentConfig.language.toString(),
                    options = AppLanguage.entries.associateBy { it.toString() },
                    onOptionSelected = { selectedLang ->
                        SettingsManager.updateConfig(currentConfig.copy(language = selectedLang))
                    }
                )
            }
            SettingsGroup(title = StringResources.settingsFolders()) {
                ClickableSettingsRow(
                    label = StringResources.foldersOpenNuclearOptionFolder(),
                    subLabel = StringResources.foldersOpenNuclearOptionFolderDesc(),
                    onClick = {
                        scope.launch {
                            Desktop.getDesktop().open(getNuclearOptionFolder())
                        }
                    })
                ClickableSettingsRow(
                    label = StringResources.foldersOpenGameFolder(),
                    subLabel = StringResources.foldersOpenGameFolderDesc(),
                    onClick = {
                        scope.launch {
                            SettingsManager.config.value.gamePath?.let {
                                Desktop.getDesktop().open(File(it))
                            }
                        }
                    })
            }
            // ── 片段 B：Folders 分组之后追加 System 分组 ──────────────────────────
            SettingsGroup(title = StringResources.settingsSystem()) {
                // OS 标识行：只读信息，显示当前运行平台
                SettingsInfoRow(
                    infoName = StringResources.systemOsLabel(),
                    infoData = getCurrentOsName()
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        if (isScrollable) {
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).padding(vertical = 16.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                adapter = rememberScrollbarAdapter(state),
                style = defaultScrollbarStyle().copy(
                    unhoverColor = MaterialTheme.colorScheme.outline,
                    hoverColor = MaterialTheme.colorScheme.primary,
                    thickness = 8.dp,
                    shape = CircleShape
                )
            )
        }
    }
}

@Composable
private fun PaletteStyle.getStringName(): String = when (this) {
    PaletteStyle.TonalSpot -> StringResources.paletteTonalSpot()
    PaletteStyle.Neutral -> StringResources.paletteNeutral()
    PaletteStyle.Vibrant -> StringResources.paletteVibrant()
    PaletteStyle.Expressive -> StringResources.paletteExpressive()
    PaletteStyle.Rainbow -> StringResources.paletteRainbow()
    PaletteStyle.FruitSalad -> StringResources.paletteFruitSalad()
    PaletteStyle.Monochrome -> StringResources.paletteMonochrome()
    PaletteStyle.Fidelity -> StringResources.paletteFidelity()
    PaletteStyle.Content -> StringResources.paletteContent()
}

@Composable
private fun Contrast.getStringName(): String = when (this) {
    Contrast.Default -> StringResources.contrastDefault()
    Contrast.Medium -> StringResources.contrastMedium()
    Contrast.High -> StringResources.contrastHigh()
    Contrast.Reduced -> StringResources.contrastReduced()
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Column(
            modifier = Modifier.clip(MaterialTheme.shapes.small).background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ClickableSettingsRow(label: String, subLabel: String, onClick: () -> Unit) {
    val shape = MaterialTheme.shapes.small

    Surface(
        onClick = onClick,
        shape = shape,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).clip(shape).pointerHoverIcon(PointerIcon.Hand)
    ) {
        Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.Center) {
            Text(label, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface)
            Text(
                subLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun <T> SettingsDropdownRow(
    label: String,
    subLabel: String,
    options: Map<String, T>,
    onOptionSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val shape = MaterialTheme.shapes.small

    Box {
        Surface(
            shape = shape,
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).clip(shape).pointerHoverIcon(PointerIcon.Hand),
            onClick = { expanded = true },
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        subLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        DropdownMenu(
            shape = MaterialTheme.shapes.small,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier, expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.key) },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                    onClick = {
                        onOptionSelected(option.value)
                        expanded = false
                    }, colors = MenuDefaults.itemColors()
                )
            }
        }
    }
}


@Composable
fun SettingsColorPicker(
    label: String,
    selectedHue: Float,
    width: Dp = 256.dp,
    onHueSelected: (Float) -> Unit,
) {
    val hueColors = remember {
        List(64) { i ->
            Color.hsv((i / 63f) * 360f, 1f, 1f)
        }
    }

    Column(modifier = Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface)

        Box(
            modifier = Modifier.width(width).height(32.dp), contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small).background(
                    Brush.horizontalGradient(
                        colors = hueColors
                    )
                ).pointerHoverIcon(PointerIcon.Hand).pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val initialHue = (down.position.x / size.width).coerceIn(0f, 1f)
                        onHueSelected(initialHue)

                        drag(down.id) { change ->
                            val newHue = (change.position.x / size.width).coerceIn(0f, 1f)
                            onHueSelected(newHue)
                            change.consume()
                        }
                    }
                })
            Box(
                Modifier.offset(x = (selectedHue * width.value).dp - 4.dp).requiredHeight(44.dp).width(8.dp)
                    .clip(CircleShape).background(MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    label: String,
    subLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {

    Surface(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).clip(MaterialTheme.shapes.small)
            .pointerHoverIcon(PointerIcon.Hand),
        onClick = { onCheckedChange(!checked) },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface)
                Text(
                    subLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Switch(
                checked = checked, onCheckedChange = null
            )
        }
    }
}


@Composable
fun SettingsInfoRow(
    infoName: String,
    infoData: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(infoName, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface)
            Text(
                infoData,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsTextFieldRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
) {
    var localText by remember(value) { mutableStateOf(value) }

    LaunchedEffect(localText) {
        if (localText != value) {
            delay(500.milliseconds)
            onValueChange(localText)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        BasicTextField(
            value = localText,
            onValueChange = { localText = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMediumEmphasized.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.border(
                        Dp.Hairline,
                        MaterialTheme.colorScheme.outline,
                        MaterialTheme.shapes.small
                    ).padding(4.dp), contentAlignment = Alignment.CenterStart
                ) {
                    if (localText.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    innerTextField()
                }
            })
    }
}