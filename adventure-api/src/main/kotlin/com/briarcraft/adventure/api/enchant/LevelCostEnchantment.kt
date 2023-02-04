package com.briarcraft.adventure.api.enchant

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

abstract class LevelCostEnchantment(key: NamespacedKey, private val baseExperienceLevelPerLevelCost: Int): Enchantment(key) {
    fun getExperienceLevelCost(level: Int) = baseExperienceLevelPerLevelCost * level
}
