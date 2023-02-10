package com.briarcraft.ahead

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

@Suppress("unused")
class AheadPlugin: SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        saveDefaultConfig()
    }

    override suspend fun onEnableAsync() {
        server.pluginManager.registerSuspendingEvents(object: Listener {
            @EventHandler()
            fun on(event: PlayerJoinEvent) {
                val player = event.player
                player.inventory.addItem(getPlayerHead(player))
            }
        }, this)
    }

    override suspend fun onDisableAsync() {
    }

    fun getPlayerHead(player: Player): ItemStack {
        val type = Material.PLAYER_HEAD
        val item = ItemStack(type, 1)

        val meta = item.itemMeta as SkullMeta
        meta.owningPlayer = player
        item.itemMeta = meta

        return item
    }
}
