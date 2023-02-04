package com.briarcraft.econ.api.item

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun <T: ItemStack> Iterable<T>.toItemAmounts() = toStackMap()
    .mapValues { (_, value) -> value.toDouble() }
    .toItemAmounts()

fun <T: ItemStack> Iterable<T>.toStackMap() = groupBy(ItemStack::getType)
    .mapValues { (_, stacks) -> stacks.sumOf(ItemStack::getAmount) }

fun Iterable<ItemAmount>.toItemStacks() = toAmountMap()
    .mapValues { (_, value) -> value.toInt() }
    .toItemStacks()

fun Iterable<ItemAmount>.toAmountMap() = groupBy(ItemAmount::type)
    .mapValues { (_, amounts) -> amounts.sumOf(ItemAmount::amount) }

fun Map<Material, Double>.toItemAmounts() = map { (type, amount) ->
    ItemAmount(type, amount)
}

fun Map<Material, Int>.toItemStacks() = flatMap { (type, amount) ->
    val stacks = (amount / type.maxStackSize)
    val remainder = (amount % type.maxStackSize)

    val list = ArrayList<ItemStack>()
    repeat(stacks) { list.add(ItemStack(type, type.maxStackSize)) }
    if (remainder > 0) list.add(ItemStack(type, remainder))

    list
}
