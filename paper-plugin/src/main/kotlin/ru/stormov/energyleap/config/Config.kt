package ru.stormov.energyleap.config

import com.google.common.base.Charsets
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

open class Config(val plugin: Plugin, val filename: String) : YamlConfiguration() {
    private val file = File(plugin.dataFolder, filename)

    init {
        file.parentFile.mkdirs()
        initDefaults()
        reloadConfig()
    }

    fun reloadConfig() {
        try {
            if (!file.exists()) {
                if (!saveDefaultConfig()) {
                    if (file.createNewFile()) {
                        setDefaults()
                        saveConfig()
                    }
                }
            }

            load(file)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    private fun initDefaults() {
        plugin.getResource(filename)?.let {
            setDefaults(loadConfiguration(InputStreamReader(it, Charsets.UTF_8)))
        }
    }

    open fun setDefaults() {
    }

    fun saveConfig() {
        save(file)
    }

    fun saveDefaultConfig(): Boolean {
        if (plugin.getResource(filename) != null) {
            plugin.saveResource(filename, true)
            return true
        }
        return false
    }

    override fun getString(path: String, def: String?): String {
        return (super.getString(path, def) ?: path).replace("/n", "\n")
    }

    override fun getString(path: String): String {
        return (super.getString(path) ?: path).replace("/n", "\n")
    }

}