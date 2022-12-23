package com.briarcraft.kotlin.util

import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryPlayer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun createEmptyPlayerInventory(player: Player): PlayerInventory = CraftInventoryPlayer(
    net.minecraft.world.entity.player.Inventory((player as CraftPlayer).handle)
)

fun isNoInventoryOpen(player: Player) = player.openInventory.type == InventoryType.CRAFTING

fun Inventory.getViewItems() = contents.filterNotNull()

fun Inventory.getFreeSlots() = contents.count { it == null || it.type.isAir }

fun Inventory.addItems(items: Iterable<ItemStack>): Map<Int, ItemStack> = addItem(*items.toList().toTypedArray())

fun Inventory.addItemAtomic(vararg items: ItemStack) =
    if (size >= contents.filterNotNull().toCountMap().union(items.toCountMap()).countItemStacks()) {
        check(addItem(*items).isEmpty())
        true
    } else false

fun Inventory.addItemsAtomic(items: Iterable<ItemStack>) =
    if (size >= contents.filterNotNull().toCountMap().union(items.toCountMap()).countItemStacks()) {
        items.forEach { check(addItem(it).isEmpty()) }
        true
    } else false

fun Inventory.removeItemAtomic(vararg items: ItemStack): Boolean {
    val itemsMap = items.toCountMap()
    return if (contents.filterNotNull().toCountMap().intersection(itemsMap) == itemsMap) {
        check(removeItem(*items).isEmpty())
        true
    } else false
}

fun Inventory.removeItemsAtomic(items: Iterable<ItemStack>): Boolean {
    val itemsMap = items.toCountMap()
    return if (contents.filterNotNull().toCountMap().intersection(itemsMap) == itemsMap) {
        items.forEach { check(removeItem(it).isEmpty()) }
        true
    } else false
}
