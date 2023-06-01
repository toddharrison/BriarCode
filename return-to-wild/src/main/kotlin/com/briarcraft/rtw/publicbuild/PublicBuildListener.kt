package com.briarcraft.rtw.publicbuild

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PublicBuildListener(private val publicBuildService: PublicBuildService): Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: PlayerQuitEvent) {
        val player = event.player
        publicBuildService.toggleOff(player)
    }
}
