package com.briarcraft.adventure.api.enchant

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

abstract class BaseEnchantment(
    key: NamespacedKey,
    private val maxLevel: Int,
    private val conflicts: Set<Enchantment>
): AbstractEnchantment(key) {
    override fun getMaxLevel() = maxLevel
    override fun conflictsWith(other: Enchantment) = conflicts.contains(other)
}