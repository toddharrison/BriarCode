package com.briarcraft.adventure.enchant

import com.briarcraft.adventure.api.enchant.getEnchants
import com.briarcraft.adventure.api.enchant.listener.ListenerNameEnchantment
import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.SmithingInventory
import org.bukkit.plugin.Plugin

class UniqueEnchantment(plugin: Plugin): ListenerNameEnchantment(
    NamespacedKey(plugin, "unique"), "Unique"
) {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PrepareResultEvent) {
        val result = event.result
        if (result != null) {
            when (val inventory = event.inventory) {
                is AnvilInventory -> {
                    if (result.enchantments.containsKey(this) || !inventory.renameText.isNullOrEmpty()) {
                        val first = inventory.firstItem
                        val firstEnchants = first?.let { getEnchants(it) } ?: mapOf()
                        val second = inventory.secondItem
                        val secondEnchants = second?.let { getEnchants(it) } ?: mapOf()
                        if ((first != null && firstEnchants.containsKey(this))
                            || (second != null && second.type != Material.ENCHANTED_BOOK && secondEnchants.containsKey(this))
                        ) {
                            event.result = null
                        }
                    }
                }
                is GrindstoneInventory -> {
                    val upper = inventory.upperItem?.let { getEnchants(it) } ?: mapOf()
                    val lower = inventory.lowerItem?.let { getEnchants(it) } ?: mapOf()
                    if (upper.containsKey(this) || lower.containsKey(this)) {
                        event.result = null
                    }
                }
                is SmithingInventory,
                is CraftingInventory -> if (result.enchantments.containsKey(this)) { event.result = null }
            }
        }
    }
}
