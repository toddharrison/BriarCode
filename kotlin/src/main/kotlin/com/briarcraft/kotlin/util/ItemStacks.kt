package com.briarcraft.kotlin.util

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.*
import kotlin.math.min

fun itemStackOf(
    material: Material,
    displayName: Component? = null,
    lore: List<Component>? = null,
    amount: Int = 1
): ItemStack {
    val itemStack = ItemStack(material, amount)
    if (displayName != null || lore != null) {
        val meta = itemStack.itemMeta
        if (displayName != null) meta.displayName(displayName)
        if (lore != null) meta.lore(lore)
        itemStack.itemMeta = meta
    }
    return itemStack
}

fun dropItemsNaturally(items: Iterable<ItemStack>, location: Location) = items
    .forEach { item -> location.world.dropItemNaturally(location, item) }

fun Iterable<ItemStack>.toCountMap(): EnumMap<Material, Int> {
    val amounts = enumMapOf<Material, Int>()
    forEach { item ->
        amounts[item.type] = (amounts[item.type] ?: 0) + item.amount
    }
    return amounts
}

fun Array<out ItemStack>.toCountMap(): EnumMap<Material, Int> {
    val amounts = enumMapOf<Material, Int>()
    forEach { item ->
        amounts[item.type] = (amounts[item.type] ?: 0) + item.amount
    }
    return amounts
}

fun EnumMap<Material, Int>.toItemStacks() =
    map { (type, amount) ->
        itemStackOf(type, amount = amount)
    }

fun EnumMap<Material, Int>.countItemStacks() =
    map { (type, amount) ->
        (amount / type.maxStackSize) + (if (amount % type.maxStackSize > 0) 1 else 0)
    }.sum()

fun EnumMap<Material, Int>.union(items: EnumMap<Material, Int>) =
    clone().let {
        items.forEach { item ->
            it[item.key] = (it[item.key] ?: 0) + item.value
        }
        it
    }

fun EnumMap<Material, Int>.intersection(items: EnumMap<Material, Int>) =
    enumMapOf<Material, Int>().also { result ->
        forEach { (type, amount) ->
            val newAmount = min(amount, items[type] ?: 0)
            if (newAmount > 0) {
                result[type] = newAmount
            }
        }
    }

/**
 * This method compares the specified ItemStack to see if they are equal, but does not consider durability (damage).
 * Params:
 * stack – the item stack to compare to
 * Returns:
 * true if the two stacks are equal, ignoring the durability
 */
fun ItemStack.equalsIgnoringDamage(obj: Any): Boolean {
    if (this === obj) return true
    if (obj !is ItemStack) return false

    return amount == obj.amount && isSimilarIgnoringDamage(obj)
}

/**
 * This method is the same as equals, but does not consider stack size (amount) or durability (damage).
 * Params:
 * stack – the item stack to compare to
 * Returns:
 * true if the two stacks are equal, ignoring the damage and amount
 */
@Suppress("DEPRECATION")
fun ItemStack.isSimilarIgnoringDamage(stack: ItemStack?): Boolean {
    if (stack == null) return false
    if (stack == this) return true
    if (hasItemMeta() != stack.hasItemMeta()) return false

    val comparisonType = if (type.isLegacy) Bukkit.getUnsafe().fromLegacy(data, true) else type
    return if (hasItemMeta()) {
        val meta = itemMeta
        val stackMeta = stack.itemMeta
        if (meta is Damageable && stackMeta is Damageable) {
            meta.damage = stackMeta.damage
            comparisonType == stack.type
                    && if (hasItemMeta()) Bukkit.getItemFactory().equals(meta, stackMeta) else true
        } else {
            comparisonType == stack.type
                    && if (hasItemMeta()) Bukkit.getItemFactory().equals(itemMeta, stack.itemMeta) else true
        }
    } else {
        comparisonType == stack.type
    }
}
