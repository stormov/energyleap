package ru.stormov.energyleap

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.stormov.energyleap.EnergyLeapPlugin.Companion.particleApi

fun Player.getEnergy() : Double = EnergyLeapPlugin.instance.energy[this] ?: 0.0
fun Player.addEnergy(amount: Double) = EnergyLeapPlugin.instance.energy.replace(this, this.getEnergy() + amount)
fun Player.takeEnergy(amount: Double) = EnergyLeapPlugin.instance.energy.replace(this, this.getEnergy() - amount)
fun Player.isUnbound() : Boolean = EnergyLeapPlugin.instance.genericConfig.getStringList("main.unbound").contains(this.uniqueId.toString())

fun Player.unbound() {
    // mega shit

    if(this.isUnbound()) return

    val current = EnergyLeapPlugin.instance.genericConfig.getStringList("unbound")
    val edit = ArrayList(current)

    edit.add(this.uniqueId.toString())

    EnergyLeapPlugin.instance.genericConfig.set("main.unbound", edit)
    EnergyLeapPlugin.instance.genericConfig.saveConfig()
    EnergyLeapPlugin.instance.genericConfig.reloadConfig()

    particleApi.LIST_1_8.DRAGON_BREATH
        .packet(true, location)
        .sendInRadiusTo(Bukkit.getOnlinePlayers(), 25.0)

    EnergyLeapPlugin.instance.energy[this] = EnergyLeapPlugin.instance.genericConfig.getDouble("main.energy-max")
}