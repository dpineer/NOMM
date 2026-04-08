package com.combat.nomm

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object LocalMods {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }
    val isBepInExInstalled: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val isGameExeFound: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val mods: StateFlow<Map<String, ModMeta>>
        field = MutableStateFlow(emptyMap())

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        loadInstalledModMetas()
    }

    fun exportMods() {
        scope.launch {
            val exportData = json.encodeToString(
                mods.value.filter { it.value.enabled == true }
                    .map { PackageReference(it.value.id, it.value.artifact?.version) }
            )

            val file = FileKit.openFileSaver(
                suggestedName = "modpack",
                extension = "nomm.json"
            )

            file?.writeString(exportData)
        }
    }

    fun addFilesToPlugins(files: List<File>) {
        val pluginsDir = File(SettingsManager.bepInExFolder, "plugins")
        if (!pluginsDir.exists()) pluginsDir.mkdirs()
        files.forEach { file ->
            val destinationFile = File(pluginsDir, file.name)
            file.moveTo(destinationFile)
        }
        refresh()
    }

    fun addFromFile() {
        scope.launch {
            val files = FileKit.openFilePicker(
                mode = FileKitMode.Multiple(),
                title = "Add From Files",
            )

            if (!files.isNullOrEmpty()) {
                addFilesToPlugins(files.map { it.file })
            }
        }
    }

    fun importMods() {
        scope.launch {
            val file = FileKit.openFilePicker(
                title = "Import Modpack",
                type = FileKitType.File(extension = "nomm.json"),
            )

            file?.let { platformFile ->
                try {
                    val jsonString = platformFile.readString()
                    val imported: List<PackageReference> = json.decodeFromString(jsonString)

                    RepoMods.fetchManifest()
                    imported.forEach {
                        mods.value[it.id] ?: run {
                            RepoMods.installMod(it.id, it.version)
                        }
                    }
                    val importedIds = imported.map { it.id }
                    mods.value.forEach { (_, meta) ->
                        if (importedIds.contains(meta.id)) {
                            meta.enable()
                        } else {
                            meta.disable()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun exportModsZip() {
        scope.launch {
            try {
                val zipFile = FileKit.openFileSaver(
                    suggestedName = "modpack",
                    extension = "zip"
                )

                zipFile?.file?.let { file ->
                    val enabledMods = mods.value.filter { it.value.enabled == true }
                    val exportData = json.encodeToString(
                        enabledMods.map { PackageReference(it.value.id, it.value.artifact?.version) }
                    )

                    file.outputStream().use { fileOut ->
                        ZipOutputStream(fileOut).use { zipOut ->
                            // 写入modpack.json
                            val modpackEntry = ZipEntry("modpack.json")
                            zipOut.putNextEntry(modpackEntry)
                            zipOut.write(exportData.toByteArray())
                            zipOut.closeEntry()

                            // 为每个启用的Mod添加文件和meta.json
                            enabledMods.forEach { (_, modMeta) ->
                                val modFile = modMeta.file ?: return@forEach
                                if (modFile.exists()) {
                                    addFileToZip(zipOut, modFile, "mods/${modMeta.id}/")
                                    
                                    // 添加meta.json
                                    val metaData = modMeta.copy(
                                        enabled = null,
                                        file = null,
                                        isUnidentified = false,
                                        hasUpdate = false,
                                        problems = emptyList()
                                    )
                                    val metaJson = json.encodeToString(metaData)
                                    val metaEntry = ZipEntry("mods/${modMeta.id}/meta.json")
                                    zipOut.putNextEntry(metaEntry)
                                    zipOut.write(metaJson.toByteArray())
                                    zipOut.closeEntry()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun importModsZip() {
        scope.launch {
            val file = FileKit.openFilePicker(
                title = "Import Modpack",
                type = FileKitType.File(extension = "zip"),
            )

            file?.let { platformFile ->
                try {
                    val tempDir = File(System.getProperty("java.io.tmpdir"), "nomm_import_${System.currentTimeMillis()}")
                    tempDir.mkdirs()

                    // 解压zip文件
                    platformFile.file.inputStream().use { fileIn ->
                        ZipInputStream(fileIn).use { zipIn ->
                            var entry = zipIn.nextEntry
                            while (entry != null) {
                                if (!entry.isDirectory) {
                                    val entryFile = File(tempDir, entry.name)
                                    entryFile.parentFile?.mkdirs()
                                    entryFile.outputStream().use { out ->
                                        zipIn.copyTo(out)
                                    }
                                }
                                entry = zipIn.nextEntry
                            }
                        }
                    }

                    // 读取modpack.json
                    val modpackJsonFile = File(tempDir, "modpack.json")
                    val imported: List<PackageReference> = if (modpackJsonFile.exists()) {
                        json.decodeFromString(modpackJsonFile.readText())
                    } else {
                        emptyList()
                    }

                    // 确保获取最新的Manifest
                    RepoMods.fetchManifest()

                    // 处理每个Mod
                    val modsDir = File(tempDir, "mods")
                    if (modsDir.exists()) {
                        modsDir.listFiles()?.forEach { modFolder ->
                            if (modFolder.isDirectory) {
                                val modId = modFolder.name
                                val existingMod = mods.value[modId]

                                // 如果Mod不存在，则从临时目录复制
                                if (existingMod == null) {
                                    val pluginsDir = File(SettingsManager.bepInExFolder, "plugins")
                                    pluginsDir.mkdirs()
                                    modFolder.copyRecursively(File(pluginsDir, modId), overwrite = true)
                                }
                            }
                        }
                    }

                    // 同步导入的Mod与本地Mod的启用/禁用状态
                    val importedIds = imported.map { it.id }
                    loadInstalledModMetas()
                    
                    mods.value.forEach { (_, meta) ->
                        if (importedIds.contains(meta.id)) {
                            meta.enable()
                        } else if (meta.enabled == true) {
                            meta.disable()
                        }
                    }

                    // 清理临时目录
                    tempDir.deleteRecursively()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun addFileToZip(zipOut: ZipOutputStream, file: File, baseDir: String) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                addFileToZip(zipOut, child, baseDir + file.name + "/")
            }
        } else {
            val entry = ZipEntry(baseDir + file.name)
            zipOut.putNextEntry(entry)
            file.inputStream().use { input ->
                input.copyTo(zipOut)
            }
            zipOut.closeEntry()
        }
    }

    fun loadInstalledModMetas() {
        val bepinexFolder = SettingsManager.bepInExFolder
        if (bepinexFolder?.exists() == true) {
            isBepInExInstalled.value = true
        } else {
            isBepInExInstalled.value = false
            mods.update { emptyMap() }
            return
        }

        isGameExeFound.value = File(SettingsManager.gameFolder, "NuclearOption.exe").exists()

        val plugins = File(bepinexFolder, "plugins").apply { mkdirs() }
        val disabled = File(bepinexFolder, "disabledPlugins").apply { mkdirs() }
        val foundMods = mutableMapOf<String, ModMeta>()

        fun scan(root: File, isEnabled: Boolean, depth: Int = 0) {
            if (depth > 10) return
            val children = root.listFiles() ?: return

            for (file in children) {
                if (file.name == "addons" || file.name == "meta.json") continue

                val metaJson = if (file.isDirectory) File(file, "meta.json") else null
                val meta = if (metaJson?.exists() == true) {
                    runCatching { RepoMods.json.decodeFromString<ModMeta>(metaJson.readText()) }.getOrNull()
                } else null

                val id = meta?.id ?: file.nameWithoutExtension
                val existing = foundMods[id]

                if (existing != null) {
                    val currentVersion = meta?.artifact?.version
                    val existingVersion = existing.artifact?.version

                    val isNewer = if (currentVersion != null && existingVersion != null) {
                        currentVersion > existingVersion
                    } else {
                        file.lastModified() > (existing.file?.lastModified() ?: 0L)
                    }

                    if (isNewer) {
                        existing.file?.deleteRecursively()
                    } else {
                        file.deleteRecursively()
                        continue
                    }
                }

                foundMods[id] = (meta ?: ModMeta(id = id)).copy(
                    file = file,
                    enabled = isEnabled,
                    isUnidentified = meta == null
                )

                if (file.isDirectory) {
                    val addonFolder = File(file, "addons")
                    if (addonFolder.exists()) scan(addonFolder, isEnabled, depth + 1)
                }
            }
        }

        scan(plugins, true)
        scan(disabled, false)

        mods.update { foundMods }
        recalculateAllProblems()
    }

    fun recalculateAllProblems() {
        mods.update { current ->
            current.mapValues { (_, meta) ->
                val repoMod = RepoMods.mods.value.find { it.id == meta.id }
                val artifact = repoMod?.artifacts?.maxByOrNull { it.version }
                val hasUpdate =
                    if (artifact == null) false else meta.artifact?.version?.let { it < artifact.version } ?: true

                val probs = meta.retrieveProblems()
                meta.copy(
                    hasUpdate = hasUpdate,
                    problems = probs,
                )
            }
        }
    }

    fun updateModState(id: String, meta: ModMeta?) {
        mods.update { current ->
            val newMap = current.toMutableMap()
            if (meta == null) newMap.remove(id) else newMap[id] = meta
            newMap
        }
        recalculateAllProblems()
    }

    fun refresh() {
        loadInstalledModMetas()
    }
}

@Serializable
data class ModMeta(
    val id: String,
    val artifact: Artifact? = null,
    @Transient val enabled: Boolean? = null,
    @Transient val file: File? = null,
    @Transient val isUnidentified: Boolean = false,
    @Transient val hasUpdate: Boolean = false,
    @Transient val problems: List<String> = emptyList(),
) {
    fun retrieveProblems(): List<String> {
        if (enabled != true) return emptyList()

        val foundProblems = mutableListOf<String>()

        artifact?.dependencies?.forEach { dep ->
            val depMod = LocalMods.mods.value[dep.id]
            if (depMod == null) {
                foundProblems.add("Dependency ${dep.id} not found")
            } else if (depMod.enabled == false) {
                foundProblems.add("Dependency ${dep.id} is disabled")
            }
        }

        artifact?.extends?.id?.let { parentId ->
            val parentMod = LocalMods.mods.value[parentId]
            if (parentMod == null) {
                foundProblems.add("Extended $parentId not found")
            } else if (parentMod.enabled == false) {
                foundProblems.add("Extended $parentId is disabled")
            }
        }

        return foundProblems
    }

    fun resolveProblems() {
        if (enabled != true) return

        artifact?.dependencies?.forEach { dep ->
            val depMod = LocalMods.mods.value[dep.id]
            if (depMod == null) {
                RepoMods.installMod(dep.id, null)
            } else if (depMod.enabled == false) {
                depMod.enable()
            }
        }

        artifact?.extends?.id?.let { parentId ->
            val parentMod = LocalMods.mods.value[parentId]
            if (parentMod == null) {
                RepoMods.installMod(parentId, null)
            } else if (parentMod.enabled == false) {
                parentMod.enable()
            }
        }
        LocalMods.refresh()
    }

    fun enable(): Boolean {
        val currentSelf = LocalMods.mods.value[id] ?: this
        val currentFile = currentSelf.file ?: return false
        if (currentSelf.enabled == true && currentFile.exists()) return true

        artifact?.extends?.id?.let { parentId ->
            val parentMod = LocalMods.mods.value[parentId] ?: return false
            if (parentMod.enabled != true) {
                val success = parentMod.enable()
                if (!success) return false
            }
        }

        val parentId = artifact?.extends?.id
        val targetDir = if (parentId != null) {
            val parentMod = LocalMods.mods.value[parentId] ?: return false
            File(parentMod.file, "addons/$id")
        } else {
            File(SettingsManager.bepInExFolder, "plugins/${currentFile.name}")
        }

        if (currentFile.moveTo(targetDir)) {
            LocalMods.updateModState(id, copy(file = targetDir, enabled = true))
            return true
        }
        return false
    }

    fun disable() {
        val currentSelf = LocalMods.mods.value[id] ?: this
        val currentFile = currentSelf.file ?: return
        if (currentSelf.enabled == false || !currentFile.exists()) return

        LocalMods.mods.value.values.forEach { other ->
            if (other.artifact?.extends?.id == id && other.enabled == true) {
                other.disable()
            }
        }

        val destination = File(SettingsManager.bepInExFolder, "disabledPlugins/${currentFile.name}")
        if (currentFile.moveTo(destination)) {
            LocalMods.updateModState(id, copy(file = destination, enabled = false))
        }
    }

    fun uninstall() {
        disable()
        (LocalMods.mods.value[id]?.file ?: file)?.deleteRecursively()
        LocalMods.updateModState(id, null)
    }

    fun update() {
        RepoMods.installMod(id, null)
    }
}

fun File.moveTo(destination: File): Boolean {
    if (!this.exists()) return false
    if (this.canonicalPath == destination.canonicalPath) return true

    return runCatching {
        destination.deleteRecursively()
        destination.parentFile?.mkdirs()
        Files.move(
            toPath(),
            destination.toPath(),
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.ATOMIC_MOVE
        )
        true
    }.getOrElse {
        runCatching {
            this.copyRecursively(destination, overwrite = true)
            this.deleteRecursively()
            true
        }.getOrDefault(false)
    }
}