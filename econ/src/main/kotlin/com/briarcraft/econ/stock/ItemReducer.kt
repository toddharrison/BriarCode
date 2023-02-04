package com.briarcraft.econ.stock

import org.bukkit.Material

fun reduceUsingMap(
    reduceMap: Map<Material, Map<Material, Double>>,
    items: Map<Material, Double>,
    recursive: Boolean = true
): Map<Material, Double> {
    return items
        .map { (type, amount) ->
            val subItems: List<Pair<Material, Double>>? = reduceMap[type]
                ?.map { (subType, subAmount) -> subType to subAmount * amount }
            if (!subItems.isNullOrEmpty()) {
                if (recursive) {
                    reduceUsingMap(reduceMap, subItems.toMap())
                } else {
                    subItems.toMap()
                }
            } else {
                mapOf(type to amount)
            }
        }
        .reduce { collection, addition ->
            val newCollection = collection.toMutableMap()
            addition.forEach { (type, amount) ->
                newCollection.merge(type, amount, Double::plus)
            }
            newCollection
        }
}
