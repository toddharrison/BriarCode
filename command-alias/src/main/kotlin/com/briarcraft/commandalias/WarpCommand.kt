package com.briarcraft.commandalias

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.configuration.Configuration
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*
import java.util.logging.Logger

class WarpCommand(
    private val logger: Logger,
    private val server: Server
) {
    private val warps: MutableMap<String, Location> = mutableMapOf()
    private val backs: MutableMap<OfflinePlayer, Location> = mutableMapOf()

    fun register() {
        CommandAPICommand("warp")
            .withPermission("commandalias.cmd.teleport.warp")
            .withArguments(
                PlayerArgument("player"),
                warpArgument("warp")
            )
            .executes(CommandExecutor { sender, args ->
                // Warp the player to the specified location
                val player = args[0] as Player
                val warpLocation = args[1] as Location
                val currentLocation = player.location
                backs[player] = currentLocation
                if (player.teleport(warpLocation, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                    sender.sendMessage("Warped ${player.name} to ${serialize(warpLocation)} from ${serialize(currentLocation)}")
                } else {
                    sender.sendMessage("Failed to warp ${player.name} to ${serialize(warpLocation)}")
                }
            }).register()

        CommandAPICommand("warpBack")
            .withPermission("commandalias.cmd.teleport.back")
            .withArguments(
                PlayerArgument("player")
            )
            .executes(CommandExecutor { sender, args ->
                // Teleport back to original location
                val player = args[0] as Player
                val previousLocation = backs[player]
                if (previousLocation != null) {
                    if (player.teleport(previousLocation, PlayerTeleportEvent.TeleportCause.COMMAND)) {
                        backs.remove(player)
                        sender.sendMessage("Returned ${player.name} back to ${serialize(previousLocation)}")
                    } else {
                        sender.sendMessage("Failed to return ${player.name} to ${serialize(previousLocation)}")
                    }
                } else {
                    sender.sendMessage("The player ${player.name} does not have a back location specified")
                }
            }).register()
    }

    fun unregister() {
        CommandAPI.unregister("warp")
        CommandAPI.unregister("warpBack")
    }

    fun load(config: Configuration) {
        config.getConfigurationSection("warps")?.let { warpsConfig ->
            warpsConfig.getKeys(false).forEach { warp ->
                val warpLocation = deserialize(warpsConfig.getString(warp))
                if (warpLocation != null) {
                    warps[warp] = warpLocation
                } else {
                    logger.warning("Error loading warp location for $warp")
                }
            }
        }

        config.getConfigurationSection("backs")?.let { backsConfig ->
            backsConfig.getKeys(false).forEach { playerId ->
                val playerUuid = UUID.fromString(playerId)
                val player = server.getOfflinePlayer(playerUuid)
                val back = deserialize(backsConfig.getString(playerId))
                if (back != null) {
                    backs[player] = back
                } else {
                    logger.warning("Error loading back location for ${player.name}")
                }
            }
        }
    }

    fun save(config: Configuration) {
        backs.forEach { (player, back) ->
            config.set("backs.${player.uniqueId}", serialize(back))
        }
        logger.warning("Saved ${backs.size} back locations")
    }

    private val locationPattern = Regex("(.+?);(-?\\d+\\.?\\d*),(-?\\d+\\.?\\d*),(-?\\d+\\.?\\d*);((-?\\d+\\.?\\d*),(-?\\d+\\.?\\d*))?")

    private fun serialize(location: Location): String {
        return "${location.world.name};${location.x},${location.y},${location.z};${location.pitch},${location.yaw}"
    }

    private fun deserialize(location: String?): Location? {
        return location?.let { loc ->
            return locationPattern.matchEntire(loc)?.let { result ->
                val world = server.getWorld(result.groupValues[1])
                val x = result.groupValues[2].toDouble()
                val y = result.groupValues[3].toDouble()
                val z = result.groupValues[4].toDouble()
                if (world != null && !x.isNaN() && !y.isNaN() && !z.isNaN()) {
                    return if (result.groupValues.size == 8 && result.groupValues[5].isNotBlank()) {
                        val pitch = result.groupValues[6].toFloat()
                        val yaw = result.groupValues[7].toFloat()
                        if (!pitch.isNaN() && !yaw.isNaN()) {
                            Location(world, x, y, z, pitch, yaw)
                        } else null
                    } else {
                        Location(world, x, y, z)
                    }
                } else null
            }
        }
    }

    private fun warpArgument(nodeName: String): Argument<Location> =
        CustomArgument(StringArgument(nodeName)) { nodeArg ->
            val name = nodeArg.input
            warps[name]
                ?: throw CustomArgument.CustomArgumentException.fromMessageBuilder(CustomArgument.MessageBuilder("That warp does not exist"))
        }.replaceSuggestions(ArgumentSuggestions.strings { _ ->
            (warps.keys.map { it }.toSet())
                .toTypedArray()
        })
}
