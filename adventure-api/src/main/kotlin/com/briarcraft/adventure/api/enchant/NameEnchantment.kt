package com.briarcraft.adventure.api.enchant

import com.briarcraft.adventure.api.enchant.usage.EnchantmentUsage
import com.briarcraft.adventure.api.enchant.usage.allEnchantmentUsage
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

open class NameEnchantment(
    key: NamespacedKey,
    override val enchantName: String
): AbstractEnchantment(key) {
    override val displayColor: TextColor = NamedTextColor.GOLD
    override val usage: EnchantmentUsage = allEnchantmentUsage

    override fun getMaxLevel() = 1
    override fun isCursed() = false
    override fun conflictsWith(other: Enchantment) = other is NameEnchantment
}