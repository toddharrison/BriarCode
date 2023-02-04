package com.briarcraft.adventure.event

import com.destroystokyo.paper.loottable.LootableInventory
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.block.Barrel
import org.bukkit.block.Chest
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.minecart.StorageMinecart
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.Plugin

class RegenerateLootEventListener(private val plugin: Plugin): Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    suspend fun on(event: LootGenerateEvent) {
        when (val holder = event.inventoryHolder) {
            is StorageMinecart -> {
                checkForRefill(event, holder)
                waitForInventoryClose(holder) {
                    holder.lootTable = event.lootTable
                }
            }
            is Chest,
            is ShulkerBox,
            is Barrel -> {
                holder.inventory.clear()
            }
        }
    }

    private fun <T> checkForRefill(event: LootGenerateEvent, holder: T) where T: InventoryHolder, T: LootableInventory {
        val now = System.currentTimeMillis()
        val remaining = holder.nextRefill - now
        if (remaining > 0) {
            event.isCancelled = true
        } else {
            holder.inventory.clear()
            holder.nextRefill = now + 1000 * 60 // TODO Generate from config
        }
    }

    private suspend fun waitForInventoryClose(holder: InventoryHolder, action: () -> Unit) {
        plugin.launch {
            do {
                delay(1.ticks)
            } while (holder.inventory.viewers.isNotEmpty())
            action()
        }
    }
}