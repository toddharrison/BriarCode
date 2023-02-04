package com.briarcraft.adventure.api.enchant.listener

import com.briarcraft.adventure.api.enchant.CurseEnchantment
import com.briarcraft.adventure.api.enchant.usage.EnchantmentUsage
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

open class ListenerCurseEnchantment(
    key: NamespacedKey,
    maxLevel: Int,
    enchantName: String,
    usage: EnchantmentUsage,
    conflicts: Set<Enchantment> = setOf()
): ListenerEnchantment, CurseEnchantment(key, maxLevel, enchantName, usage, conflicts)
