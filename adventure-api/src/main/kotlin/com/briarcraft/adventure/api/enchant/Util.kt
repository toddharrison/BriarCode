package com.briarcraft.adventure.api.enchant

import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import kotlin.math.min

fun getEnchants(item: ItemStack): Map<Enchantment, Int> {
    return item.itemMeta.let { meta ->
        if (meta is EnchantmentStorageMeta) {
            check(item.enchantments.isEmpty())
            meta.storedEnchants
        } else item.enchantments
    }
}

fun mergeEnchants(firstEnchants: Map<Enchantment, Int>, secondEnchants: Map<Enchantment, Int>): Map<Enchantment, Int> {
    val firstEnchantsSet = firstEnchants.keys
    val enchants = firstEnchants.toMutableMap()
    secondEnchants
        .filter { (enchant, _) ->
            firstEnchantsSet.none { e -> e != enchant && e.conflictsWith(enchant) }
        }
        .forEach { (enchant, level) ->
            val curLevel = enchants[enchant]
            if (curLevel == null) {
                enchants[enchant] = level
            } else {
                if (level > curLevel) enchants[enchant] = level
                else if (level < curLevel) enchants[enchant] = curLevel
                else enchants[enchant] = min(enchant.maxLevel, level + 1)
            }
        }
    return enchants
}

fun clearEnchants(item: ItemStack) {
    item.enchantments.forEach { (enchant, _) -> item.removeEnchantment(enchant) }
}

fun updateEnchantingLore(item: ItemStack) {
    val meta = item.itemMeta
    val lore = getEnchants(item)
        .filterNot { it.key.key.namespace == "minecraft" }
        .map { (enchant, level) ->
            enchant.displayName(level).decoration(TextDecoration.ITALIC, false)
        }
    meta.lore(lore)
    item.itemMeta = meta
}

fun clearLore(item: ItemStack) {
    val meta = item.itemMeta
    meta.lore(null)
    item.itemMeta = meta
}

fun getRepairCost(item: ItemStack): Int {
    return min(getEnchants(item).map { (enchant, level) ->
        when (enchant) {
            is LevelCostEnchantment -> enchant.getExperienceLevelCost(level)

            Enchantment.SILK_TOUCH,
            Enchantment.SOUL_SPEED,
            Enchantment.SWIFT_SNEAK,
            Enchantment.CHANNELING,
            Enchantment.ARROW_INFINITE,
            Enchantment.BINDING_CURSE,
            Enchantment.VANISHING_CURSE,
            Enchantment.THORNS -> 4 * level

            Enchantment.MENDING,
            Enchantment.RIPTIDE,
            Enchantment.IMPALING,
            Enchantment.ARROW_FIRE,
            Enchantment.ARROW_KNOCKBACK,
            Enchantment.MULTISHOT,
            Enchantment.LUCK,
            Enchantment.LURE,
            Enchantment.LOOT_BONUS_MOBS,
            Enchantment.LOOT_BONUS_BLOCKS,
            Enchantment.FIRE_ASPECT,
            Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.WATER_WORKER,
            Enchantment.OXYGEN,
            Enchantment.DEPTH_STRIDER,
            Enchantment.FROST_WALKER,
            Enchantment.SWEEPING_EDGE -> 2 * level

            else -> 1 * level
        }
    }.sum(), 39)
}
