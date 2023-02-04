package com.briarcraft.rtw.change.player

import com.briarcraft.rtw.util.clearContext
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(
    private val plugin: SuspendingJavaPlugin,
    private val repository: PlayerLogoffRepository,
    private val spawnLoc: Location = Bukkit.getWorld(NamespacedKey.fromString("minecraft:overworld")!!)?.spawnLocation!!
): Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun on(event: PlayerJoinEvent) {
        val player = event.player
        player.clearContext()

        val loc = player.location
        loc.chunk.addPluginChunkTicket(plugin)
        if (!isEmptyOrPassable(loc)) {
            spawnLoc.chunk.addPluginChunkTicket(plugin)
            player.teleport(spawnLoc)
            spawnLoc.chunk.removePluginChunkTicket(plugin)
        }
        loc.chunk.removePluginChunkTicket(plugin)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun on(event: PlayerQuitEvent) {
        val player = event.player
        val name = player.name
        val id = player.uniqueId
        val location = player.location
        repository.save(PlayerLogoff(name, id, location))
    }

    private fun isEmptyOrPassable(loc: Location): Boolean {
        val footBlock = loc.block
        val headBlock = footBlock.getRelative(BlockFace.UP)
        return !headBlock.isSolid
    }
}
