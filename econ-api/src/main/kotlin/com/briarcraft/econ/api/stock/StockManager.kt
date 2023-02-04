package com.briarcraft.econ.api.stock

import com.briarcraft.econ.api.item.ItemAmount
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface StockManager {
    fun getItems(): Set<Material>
    fun getStock(type: Material): Stock?
    fun add(item: ItemStack): Boolean?
    fun add(items: Iterable<ItemStack>): Boolean?
    fun add(item: ItemAmount): Boolean?
    fun canAdd(item: ItemStack): Boolean?
    fun canAdd(items: Iterable<ItemStack>): Boolean?
    fun canAdd(item: ItemAmount): Boolean?
    fun remove(item: ItemStack): Boolean?
    fun remove(items: Iterable<ItemStack>): Boolean?
    fun remove(item: ItemAmount): Boolean?
    fun canRemove(item: ItemStack): Boolean?
    fun canRemove(items: Iterable<ItemStack>): Boolean?
    fun canRemove(item: ItemAmount): Boolean?
}
