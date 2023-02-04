package com.briarcraft.adventure.api.enchant.usage

import org.bukkit.Material
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.Damageable

@Suppress("DEPRECATION")
val allEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.ALL, setOf(*EquipmentSlot.values())) { true }
val armorEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.ARMOR, setOf(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD)) {
    when (it.type) {
        Material.LEATHER_HELMET,
        Material.IRON_HELMET,
        Material.GOLDEN_HELMET,
        Material.DIAMOND_HELMET,
        Material.NETHERITE_HELMET,

        Material.LEATHER_CHESTPLATE,
        Material.IRON_CHESTPLATE,
        Material.GOLDEN_CHESTPLATE,
        Material.DIAMOND_CHESTPLATE,
        Material.NETHERITE_CHESTPLATE,

        Material.LEATHER_LEGGINGS,
        Material.IRON_LEGGINGS,
        Material.GOLDEN_LEGGINGS,
        Material.DIAMOND_LEGGINGS,
        Material.NETHERITE_LEGGINGS,

        Material.LEATHER_BOOTS,
        Material.IRON_BOOTS,
        Material.GOLDEN_BOOTS,
        Material.DIAMOND_BOOTS,
        Material.NETHERITE_BOOTS -> true

        else -> false
    }
}
val bootsEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.ARMOR_FEET, setOf(EquipmentSlot.FEET)) {
    when (it.type) {
        Material.LEATHER_BOOTS,
        Material.IRON_BOOTS,
        Material.GOLDEN_BOOTS,
        Material.DIAMOND_BOOTS,
        Material.NETHERITE_BOOTS -> true

        else -> false
    }
}
val helmetEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.ARMOR_HEAD, setOf(EquipmentSlot.HEAD)) {
    when (it.type) {
        Material.LEATHER_HELMET,
        Material.IRON_HELMET,
        Material.GOLDEN_HELMET,
        Material.DIAMOND_HELMET,
        Material.NETHERITE_HELMET -> true

        else -> false
    }
}
val leggingsEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.ARMOR_LEGS, setOf(EquipmentSlot.LEGS)) {
    when (it.type) {
        Material.LEATHER_LEGGINGS,
        Material.IRON_LEGGINGS,
        Material.GOLDEN_LEGGINGS,
        Material.DIAMOND_LEGGINGS,
        Material.NETHERITE_LEGGINGS -> true

        else -> false
    }
}
val chestplateEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.ARMOR_TORSO, setOf(EquipmentSlot.CHEST)) {
    when (it.type) {
        Material.LEATHER_CHESTPLATE,
        Material.IRON_CHESTPLATE,
        Material.GOLDEN_CHESTPLATE,
        Material.DIAMOND_CHESTPLATE,
        Material.NETHERITE_CHESTPLATE -> true

        else -> false
    }
}
val swordEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.WEAPON, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.WOODEN_SWORD,
        Material.IRON_SWORD,
        Material.GOLDEN_SWORD,
        Material.DIAMOND_SWORD,
        Material.NETHERITE_SWORD -> true

        else -> false
    }
}
val weaponEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.BREAKABLE, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.TRIDENT,

        Material.WOODEN_SWORD,
        Material.IRON_SWORD,
        Material.GOLDEN_SWORD,
        Material.DIAMOND_SWORD,
        Material.NETHERITE_SWORD,

        Material.WOODEN_AXE,
        Material.IRON_AXE,
        Material.GOLDEN_AXE,
        Material.DIAMOND_AXE,
        Material.NETHERITE_AXE -> true

        else -> false
    }
}
val toolEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.TOOL, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.WOODEN_SHOVEL,
        Material.IRON_SHOVEL,
        Material.GOLDEN_SHOVEL,
        Material.DIAMOND_SHOVEL,
        Material.NETHERITE_SHOVEL,

        Material.WOODEN_PICKAXE,
        Material.IRON_PICKAXE,
        Material.GOLDEN_PICKAXE,
        Material.DIAMOND_PICKAXE,
        Material.NETHERITE_PICKAXE,

        Material.WOODEN_AXE,
        Material.IRON_AXE,
        Material.GOLDEN_AXE,
        Material.DIAMOND_AXE,
        Material.NETHERITE_AXE,

        Material.WOODEN_HOE,
        Material.IRON_HOE,
        Material.GOLDEN_HOE,
        Material.DIAMOND_HOE,
        Material.NETHERITE_HOE -> true

        else -> false
    }
}
val rangedEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.BREAKABLE, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.BOW,
        Material.CROSSBOW,
        Material.TRIDENT -> true

        else -> false
    }
}
val bowEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.BOW, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.BOW -> true

        else -> false
    }
}
val crossbowEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.CROSSBOW, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.CROSSBOW -> true

        else -> false
    }
}
val tridentEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.TRIDENT, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.TRIDENT -> true

        else -> false
    }
}
val shieldEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.BREAKABLE, setOf(EquipmentSlot.OFF_HAND)) {
    when (it.type) {
        Material.SHIELD -> true

        else -> false
    }
}
val fishingRodEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.FISHING_ROD, setOf(EquipmentSlot.HAND)) {
    when (it.type) {
        Material.FISHING_ROD -> true

        else -> false
    }
}
val breakableEnchantmentUsage = EnchantmentUsage(EnchantmentTarget.BREAKABLE, setOf(*EquipmentSlot.values())) { it.itemMeta is Damageable }
