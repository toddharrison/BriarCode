package com.briarcraft.adventure.event

import org.bukkit.block.Barrel
import org.bukkit.block.Chest
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.minecart.StorageMinecart
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.plugin.Plugin

class RegenerateLootEventListener(private val plugin: Plugin): Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: LootGenerateEvent) {
        when (val holder = event.inventoryHolder) {
            is StorageMinecart,
            is Chest,
            is ShulkerBox,
            is Barrel -> {
                holder.inventory.clear()
            }
        }
    }
}
