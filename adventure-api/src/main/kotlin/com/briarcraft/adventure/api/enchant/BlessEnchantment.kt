package com.briarcraft.adventure.api.enchant

import com.briarcraft.adventure.api.enchant.usage.EnchantmentUsage
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

open class BlessEnchantment(
    key: NamespacedKey,
    maxLevel: Int,
    override val enchantName: String,
    override val usage: EnchantmentUsage,
    conflicts: Set<Enchantment> = setOf()
): BaseEnchantment(key, maxLevel, conflicts) {
    override val displayColor: TextColor = NamedTextColor.GRAY

    override fun isCursed() = false
}