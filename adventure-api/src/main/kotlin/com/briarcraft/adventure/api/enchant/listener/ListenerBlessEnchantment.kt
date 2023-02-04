package com.briarcraft.adventure.api.enchant.listener

import com.briarcraft.adventure.api.enchant.BlessEnchantment
import com.briarcraft.adventure.api.enchant.usage.EnchantmentUsage
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

open class ListenerBlessEnchantment(
    key: NamespacedKey,
    maxLevel: Int,
    enchantName: String,
    usage: EnchantmentUsage,
    conflicts: Set<Enchantment> = setOf()
): ListenerEnchantment, BlessEnchantment(key, maxLevel, enchantName, usage, conflicts)
