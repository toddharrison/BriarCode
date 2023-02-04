package com.briarcraft.econ.stock

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.stock.Stock
import com.briarcraft.econ.api.stock.StockAmount
import com.briarcraft.econ.api.stock.StockManager
import com.briarcraft.kotlin.util.allNullable
import com.briarcraft.kotlin.util.asEnumSet
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.HashMap

class FiniteStockManager(items: Map<Material, StockAmount>): StockManager {
    private val items = HashMap(items)
    private val itemSet = items.keys.asEnumSet()

    override fun getItems(): Set<Material> = itemSet
    override fun getStock(type: Material) = items[type]?.let {
        Stock(it.curAmount, it.maxAmount?.minus(it.curAmount))
    }
    override fun add(item: ItemStack) = canAdd(item).also {
        if (it == true) {
            items[item.type]!!.curAmount += item.amount
        }
    }
    // TODO Optimize
    override fun add(items: Iterable<ItemStack>) = if (canAdd(items) == true) {
        items.allNullable { add(it) }
    } else null
    override fun add(item: ItemAmount) = canAdd(item).also {
        if (it == true) {
            items[item.type]!!.curAmount += item.amount
        }
    }
    override fun canAdd(item: ItemStack) = items[item.type]?.let {
        it.maxAmount == null || it.maxAmount!! - it.curAmount >= item.amount
    }
    override fun canAdd(items: Iterable<ItemStack>) = items.allNullable { canAdd(it) }
    override fun canAdd(item: ItemAmount) = items[item.type]?.let {
        it.maxAmount == null || it.maxAmount!! - it.curAmount >= item.amount
    }
    override fun remove(item: ItemStack) = canRemove(item).also {
        if (it == true) {
            items[item.type]!!.curAmount -= item.amount
        }
    }
    // TODO Optimize
    override fun remove(items: Iterable<ItemStack>) = if (canRemove(items) == true) {
        items.allNullable { remove(it) }
    } else null
    override fun remove(item: ItemAmount) = canRemove(item).also {
        if (it == true) {
            items[item.type]!!.curAmount -= item.amount
        }
    }
    override fun canRemove(item: ItemStack) = items[item.type]?.let {
        it.curAmount >= item.amount
    }
    override fun canRemove(items: Iterable<ItemStack>) = items.allNullable { canRemove(it) }
    override fun canRemove(item: ItemAmount) = items[item.type]?.let {
        it.curAmount >= item.amount
    }
}
