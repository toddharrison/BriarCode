package com.briarcraft.econ.api.stock

import org.bukkit.Material

interface ReducingStockManager: StockManager {
    fun getBaseItems(): Set<Material>
    fun reduce(items: Map<Material, Double>): Map<Material, Double>
}
