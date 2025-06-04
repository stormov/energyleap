package ru.stormov.energyleap

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.stormov.energyleap.commands.EnergyLeapCommand
import ru.stormov.energyleap.config.Config
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext

class EnergyLeapPlugin : JavaPlugin(), Listener, CoroutineScope {

    val genericConfig = Config(this, "config.yml")

    val energy: HashMap<Player, Double> = HashMap()

    override fun onEnable() {
        instance = this
        particleApi = ParticleNativeCore.loadAPI(this)

        logger.info("Particle API class: ${particleApi.javaClass.name}")
        logger.info("Configuration file: ${genericConfig.currentPath}/${genericConfig.filename}")

        if(!genericConfig.contains("main")) {
            genericConfig.set("main.unbound", listOf<String>())
            genericConfig.set("main.energy-cost-per-jump", 100.0)
            genericConfig.set("main.energy-max", 500.0)

            genericConfig.saveConfig()
            genericConfig.reloadConfig()
        }

        server.pluginManager.registerEvents(this, this)

        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            Bukkit.getOnlinePlayers().filter { it.isUnbound() && it.getEnergy() <= genericConfig.getDouble("main.energy-max") }
                .forEach { it.addEnergy(1.0) }
        }, 20L, 20L)

        getCommand("energyleap")?.setExecutor(EnergyLeapCommand)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        if(player.isUnbound() && !energy.containsKey(player)) energy[player] = genericConfig.getDouble("main.energy-max")
    }

    @EventHandler
    fun onHandSwap(event: PlayerSwapHandItemsEvent) {
        val player = event.player

        if(!player.isUnbound()) return

        player.sendActionBar(Component.text("Энергия: ${player.getEnergy()}/${genericConfig.getDouble("main.energy-max")}"))
    }

    companion object {
        lateinit var particleApi: ParticleNativeAPI
        lateinit var instance: EnergyLeapPlugin
    }

    fun jump(from: Location, loc: Location, player: Player) {
        if(player.getEnergy() <= genericConfig.getDouble("main.energy-cost-per-jump")) {
            player.sendMessage(Component.text("Недостаточно энергии для совершения прыжка").color(NamedTextColor.RED))
            return
        }

        launch {
            repeat(25) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this@EnergyLeapPlugin) {
                    particleApi.LIST_1_8.TOTEM
                        .packet(true, from.clone().add(0.0, 1.0, 0.0))
                        .sendTo(Bukkit.getOnlinePlayers())

                    particleApi.LIST_1_8.TOTEM
                        .packet(true, loc.clone().add(0.0, 1.0, 0.0))
                        .sendTo(Bukkit.getOnlinePlayers())
                }
            }
        }

        launch {
            delay(500L)

            Bukkit.getScheduler().scheduleSyncDelayedTask(this@EnergyLeapPlugin) {
                player.teleport(loc)
            }

            player.takeEnergy(genericConfig.getDouble("main.energy-cost-per-jump"))
        }
    }

    override val coroutineContext: CoroutineContext
        get() = CoroutineName("EnergyLeap")

}