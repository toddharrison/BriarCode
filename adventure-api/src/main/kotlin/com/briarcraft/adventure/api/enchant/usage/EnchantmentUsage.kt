package com.briarcraft.adventure.api.enchant.usage

import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

data class EnchantmentUsage(
    val target: EnchantmentTarget,
    val activeSlots: Set<EquipmentSlot>,
    val canEnchant: (ItemStack) -> Boolean
)