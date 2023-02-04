package com.briarcraft.adventure.event

import com.briarcraft.adventure.api.enchant.*
import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.meta.EnchantmentStorageMeta

class CustomEnchantListener: Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: PrepareResultEvent) {
        when (val inventory = event.inventory) {
            is AnvilInventory -> {
                val first = inventory.firstItem
                val second = inventory.secondItem

                if (first != null && second != null) {
                    val firstEnchants = getEnchants(first)
                    val secondEnchants = getEnchants(second)

                    var result = event.result
                    if (result == null) {
                        if (first.type == second.type || second.itemMeta is EnchantmentStorageMeta) {
                            result = first.clone()
                        } else return
                    }
                    if (result.type == Material.ENCHANTED_BOOK) {
                        val meta = result.itemMeta as EnchantmentStorageMeta
                        mergeEnchants(firstEnchants, secondEnchants).forEach { (enchant, level) ->
                            meta.addStoredEnchant(enchant, level, false)
                        }
                        result.itemMeta = meta
                    } else {
                        clearEnchants(result)
                        mergeEnchants(firstEnchants, secondEnchants).forEach { (enchant, level) ->
                            if (enchant.canEnchantItem(result)) {
                                result.addUnsafeEnchantment(enchant, level)
                            }
                        }
                    }
                    updateEnchantingLore(result)
                    event.result = result
                    inventory.repairCost = getRepairCost(result)

                    if (!inventory.renameText.isNullOrEmpty()) inventory.repairCost++
                }
            }
            is GrindstoneInventory -> {
                val result = event.result

                if (result != null) {
                    clearLore(result)
                }
            }
        }
    }
}
