//package com.briarcraft.econ.util
//
//import com.briarcraft.kotlin.util.enumMapOf
//import com.briarcraft.kotlin.util.toEnumMap
//import org.bukkit.Material
//import java.util.*
//
//fun simplifyItems(mappings: Map<Material, Map<Material, Double>>, items: Map<Material, Double>): EnumMap<Material, Double> {
//    return items
//        .map { (type, amount) ->
//            val subItems = mappings[type]
//                ?.map { (subType, subAmount) -> subType to subAmount * amount }
//            if (!subItems.isNullOrEmpty()) {
//                simplifyItems(mappings, subItems.toEnumMap())
//            } else {
//                enumMapOf(type to amount)
//            }
//        }.reduce { collection, addition ->
//            addition.forEach { (type, amount) ->
//                collection.merge(type, amount, Double::plus)
//            }
//            collection
//        }
//}
