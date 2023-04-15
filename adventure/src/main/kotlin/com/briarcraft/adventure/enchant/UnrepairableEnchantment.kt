package com.briarcraft.adventure.enchant

import com.briarcraft.adventure.api.enchant.listener.ListenerCurseEnchantment
import com.briarcraft.adventure.api.enchant.usage.breakableEnchantmentUsage
import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.meta.Damageable
import org.bukkit.plugin.Plugin
import kotlin.math.max

class UnrepairableEnchantment(plugin: Plugin): ListenerCurseEnchantment(
    NamespacedKey(plugin, "unrepairable"), 1, "Unrepairable", breakableEnchantmentUsage, setOf(MENDING)
) {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun on(event: PrepareResultEvent) {
        val result = event.result
        val items = when (val inventory = event.inventory) {
            is AnvilInventory -> inventory.firstItem to inventory.secondItem
            else -> null to null
        }

        val first = items.first
        val second = items.second
        if (result != null && result.enchantments.containsKey(this)) {
            val resultMeta = result.itemMeta
            if (resultMeta is Damageable) {
                val firstDamage = if (first != null) {
                    val firstMeta = first.itemMeta
                    if (firstMeta is Damageable) {
                        firstMeta.damage
                    } else 0
                } else 0

                val secondDamage = if (second != null) {
                    val secondMeta = second.itemMeta
                    if (secondMeta is Damageable) {
                        secondMeta.damage
                    } else 0
                } else 0

                resultMeta.damage = max(firstDamage, secondDamage)
                result.itemMeta = resultMeta
            }
        }
    }
}
