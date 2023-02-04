//package com.briarcraft.econ.util
//
//import org.bukkit.configuration.ConfigurationSection
//
//inline fun <T> ConfigurationSection.mapSections(
//    action: (String, ConfigurationSection) -> T
//): List<T> {
//    return this.getKeys(false).mapNotNull { key ->
//        val section = this.getConfigurationSection(key)
//        if (section != null) {
//            action(key, section)
//        } else null
//    }
//}
//
//inline fun <K, V> ConfigurationSection.mapValues(
//    mapKey: (String) -> K,
//    mapValue: (ConfigurationSection, String) -> V?
//): Map<K, V> {
//    return this.getKeys(false).mapNotNull { key ->
//        val newValue = mapValue(this, key)
//        if (newValue != null) {
//            mapKey(key) to newValue
//        } else null
//    }.toMap()
//}
