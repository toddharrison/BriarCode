package com.briarcraft.kotlin.util

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.EnumMap
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
