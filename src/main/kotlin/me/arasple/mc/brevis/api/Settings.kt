package me.arasple.mc.brevis.api

import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Coerce
import me.arasple.mc.brevis.Brevis
import me.arasple.mc.brevis.module.shortcut.Shortcut
import me.arasple.mc.brevis.module.shortcut.Track
import me.arasple.mc.brevis.module.shortcut.TrackType
import me.arasple.mc.brevis.util.getFiles
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

/**
 * @author Arasple
 * @date 2021/2/14 16:58
 */
class Settings {

    val maxTrack by lazy {
        CONF.getInt("Shortcut.Max-Cache-Track", 5).coerceAtLeast(2)
    }

//    val shortcuts by lazy {
//        val start = System.nanoTime()
//        CONF.getConfigurationSection("Register")?.getKeys(false)!!.mapNotNull {
//            CONF.getConfigurationSection("Register.$it")?.run {
//                buildShortcut(this@run)
//            }
//        }.also {
//            TLocale.sendToConsole("Shortcut.Loaded", it.size, Coerce.format((System.nanoTime() - start).div(1000000.0)))
//        }
//    }

    private val folder by lazy {
        File(Brevis.plugin.dataFolder, "Shortcuts")
    }

    val shortcuts by lazy {
        val start = System.nanoTime()
        mutableMapOf<String, Shortcut>().apply {
            getFiles(folder).map { YamlConfiguration.loadConfiguration(it) }.forEach { config ->
                config.getKeys(false).forEach { key ->
                    buildShortcut(config.getConfigurationSection(key))?.let { put(key, it) }
                }
            }
        }.also {
            TLocale.sendToConsole("Shortcut.Loaded", it.size, Coerce.format((System.nanoTime() - start).div(1000000.0)))
        }
    }

    private fun buildShortcut(section: ConfigurationSection?): Shortcut? {
        if (section == null) return null
        val tracks = section.getStringList("courses").mapNotNull { line ->
            val course = line.split(";", limit = 2)
            val span = course.getOrNull(1)?.toLong() ?: -1L
            val name = course[0].split("-", limit = 2)
            val type = TrackType.of(name[0])
            if (type == null) null
            else {
                val value = name.getOrNull(1)?.toInt() ?: -1
                Track(type, value, span)
            }
        }
        val reaction = section.get("reaction")
        val react = if (reaction is List<*>) reaction.joinToString("\n") { line -> line.toString() } else reaction.toString()

        if (tracks.isNotEmpty()) {
            return Shortcut(
                global = section.getBoolean("global", false),
                courses = tracks,
                reaction = react
            )
        }
        return null
    }

    companion object {

        @TInject("settings.yml", locale = "Options.Language", migrate = true)
        private lateinit var CONF: TConfig

        internal var INSTANCE = Settings()

        fun init() {
            CONF.listener { onReload() }.also { onReload() }

        }

        fun onReload() {
            INSTANCE = Settings()
            INSTANCE.shortcuts
        }

    }

}