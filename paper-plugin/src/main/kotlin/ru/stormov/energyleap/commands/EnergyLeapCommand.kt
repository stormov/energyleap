package ru.stormov.energyleap.commands

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.stormov.energyleap.EnergyLeapPlugin
import ru.stormov.energyleap.isUnbound
import ru.stormov.energyleap.unbound

object EnergyLeapCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<String>): Boolean {
        if(args.isEmpty()) {
            sender.sendMessage("usage: /energyleap unbound <username>\n /energyleap jump <worldName> <x> <y> <z>")
            return true
        }

        when(args[0]) {
            "unbound" -> {
                if(!sender.hasPermission("energyleap.command.unbound")) return true
                val name = args[1]

                val player = Bukkit.getPlayer(name) ?: return true

                if(player.isUnbound()) return true

                player.unbound()
            }

            "jump" -> {
                if(sender !is Player) return true
                if(!sender.hasPermission("energyleap.command.jump")) return true
                if(!sender.isUnbound()) return false
                if(args.size <= 4) {
                    sender.sendMessage("usage: /energyleap unbound <username>\n /energyleap jump <worldName> <x> <y> <z>")
                    return true
                }

                val world = Bukkit.getWorld(args[1]) ?: return false
                val x = args[2].toDouble()
                val y = args[3].toDouble()
                val z = args[4].toDouble()

                val loc = Location(world, x, y, z)

                EnergyLeapPlugin.instance.jump(sender.location, loc, sender)

                return true
            }
        }
        return true
    }
}