package com.briarcraft.adventure.api.enchant.potion

import com.briarcraft.adventure.api.enchant.BlessEnchantment
import com.briarcraft.adventure.api.enchant.usage.EnchantmentUsage
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.potion.PotionEffectType

class PotionBlessEffectEnchantment(
    key: NamespacedKey,
    maxLevel: Int,
    enchantName: String,
    usage: EnchantmentUsage,
    override val effectType: PotionEffectType,
    conflicts: Set<Enchantment> = setOf()
): PotionEffectEnchantment, BlessEnchantment(key, maxLevel, enchantName, usage, conflicts)
