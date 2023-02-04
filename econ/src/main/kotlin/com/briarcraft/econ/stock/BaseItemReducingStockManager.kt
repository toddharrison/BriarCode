package com.briarcraft.econ.stock

import com.briarcraft.econ.api.item.ItemAmount
import com.briarcraft.econ.api.item.toItemAmounts
import com.briarcraft.econ.api.stock.ReducingStockManager
import com.briarcraft.econ.api.stock.Stock
import com.briarcraft.econ.api.stock.StockAmount
import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.HashMap

class BaseItemReducingStockManager(
    reduceItems: Map<Material, Map<Material, Double>>,
    items: Map<Material, StockAmount>
): ReducingStockManager {
    private val reduceItems: Map<Material, Map<Material, Double>> = reduceItems
        .mapValues { (_, map) -> reduceUsingMap(reduceItems, map) }
    private val items = HashMap(items)
    private val itemSet = (items.keys + reduceItems.keys + reduceItems.map { it.key }).toEnumSet()

    init {
        require(reduceItems.keys.none { items.containsKey(it) }) { "Overlap Error: ${reduceItems.keys.intersect(items.keys)}" }
        require(reduceItems.flatMap { it.value.keys }.toSet().all { reduceItems.containsKey(it) || items.containsKey(it) })
    }

    override fun getItems(): Set<Material> = itemSet
    override fun getBaseItems(): Set<Material> = items.keys
    override fun getStock(type: Material) = items[type]?.let {
        Stock(it.curAmount, it.maxAmount?.minus(it.curAmount))
    } ?: if (reduceItems.keys.contains(type)) {
        reduce(mapOf(type to 1.0))
            .map { (subType, subAmount) ->
                items[subType]?.let { (it.curAmount / subAmount).toInt() to (it.maxAmount?.div(subAmount))?.toInt() } ?: return null
            }.let { pairs ->
                val cur = pairs.minOf { it.first }.toDouble()
                val max = if (pairs.any { it.second == null }) null else {
                    pairs.minOfOrNull { it.second!! }
                }?.toDouble()
                Stock(cur, max?.minus(cur))
            }
    } else null
    override fun add(item: ItemStack) = canAdd(ItemAmount(item.type, item.amount.toDouble())) { stocks ->
        stocks.forEach { (stock, amount) ->
            stock.curAmount += amount
        }
    }
    override fun add(items: Iterable<ItemStack>) = canAdd(items.toItemAmounts()) { stocks ->
        stocks.forEach { (stock, amount) ->
            stock.curAmount += amount
        }
    }
    override fun add(item: ItemAmount) = canAdd(item) { stocks ->
        stocks.forEach { (stock, amount) ->
            stock.curAmount += amount
        }
    }
    override fun canAdd(item: ItemStack) = canAdd(ItemAmount(item.type, item.amount.toDouble())) {}
    override fun canAdd(items: Iterable<ItemStack>) = canAdd(items.toItemAmounts()) {}
    override fun canAdd(item: ItemAmount) = canAdd(item) {}
    override fun remove(item: ItemStack) = canRemove(ItemAmount(item.type, item.amount.toDouble())) { stocks ->
        stocks.forEach { (stock, amount) ->
            stock.curAmount -= amount
        }
    }
    override fun remove(items: Iterable<ItemStack>) = canRemove(items.toItemAmounts()) { stocks ->
        stocks.forEach { (stock, amount) ->
            stock.curAmount -= amount
        }
    }
    override fun remove(item: ItemAmount) = canRemove(item) { stocks ->
        stocks.forEach { (stock, amount) ->
            stock.curAmount -= amount
        }
    }
    override fun canRemove(item: ItemStack) = canRemove(ItemAmount(item.type, item.amount.toDouble())) {}
    override fun canRemove(items: Iterable<ItemStack>) = canRemove(items.toItemAmounts()) {}
    override fun canRemove(item: ItemAmount) = canRemove(item) {}



    private fun canAdd(item: ItemAmount, action: (List<Pair<StockAmount, Double>>) -> Unit): Boolean? {
        return can(item, action) { it, amount ->
            it.maxAmount == null || it.maxAmount!! - it.curAmount >= amount
        }
    }

    private fun canAdd(items: Iterable<ItemAmount>, action: (List<Pair<StockAmount, Double>>) -> Unit): Boolean? {
        return can(items, action) { it, amount ->
            it.maxAmount == null || it.maxAmount!! - it.curAmount >= amount
        }
    }

    private fun canRemove(item: ItemAmount, action: (List<Pair<StockAmount, Double>>) -> Unit): Boolean? {
        return can(item, action) { it, amount ->
            it.curAmount >= amount
        }
    }

    private fun canRemove(items: Iterable<ItemAmount>, action: (List<Pair<StockAmount, Double>>) -> Unit): Boolean? {
        return can(items, action) { it, amount ->
            it.curAmount >= amount
        }
    }

    private fun can(item: ItemAmount, action: (List<Pair<StockAmount, Double>>) -> Unit, test: (StockAmount, Double) -> Boolean): Boolean? {
        return items[item.type]?.let {
            (test(it, item.amount)).also { can ->
                if (can) action(listOf(it to item.amount))
            }
        } ?: reduce(mapOf(item.type to item.amount)).let {
            if (items.keys.containsAll(it.keys)) {
                val amounts = it.map { (type, amount) -> items[type]!! to amount }
                if (amounts.all { (stock, amount) -> test(stock, amount) }) {
                    action(amounts)
                    true
                } else false
            } else null
        }
    }

    private fun can(items: Iterable<ItemAmount>, action: (List<Pair<StockAmount, Double>>) -> Unit, test: (StockAmount, Double) -> Boolean): Boolean? {
        return reduce(items.associate { it.type to it.amount }).let {
            if (this.items.keys.containsAll(it.keys)) {
                val amounts = it.map { (type, amount) -> this.items[type]!! to amount }
                if (amounts.all { (stock, amount) -> test(stock, amount) }) {
                    action(amounts)
                    true
                } else false
            } else null
        }
    }

    override fun reduce(items: Map<Material, Double>) = reduceUsingMap(reduceItems, items, false)
}
