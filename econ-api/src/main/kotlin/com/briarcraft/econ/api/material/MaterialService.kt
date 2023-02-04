package com.briarcraft.econ.api.material

import org.bukkit.Material
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

interface MaterialService {
    val plugin: Plugin
    val allItems: MaterialSet
    val baseItems: MaterialSet
    val materialSets: Map<String, MaterialSet>
    val reduceItems: Map<Material, Map<Material, Double>>
    val excludedItems: MaterialSet

    fun registerService() = plugin.server.servicesManager.let { servicesManager ->
        if (!servicesManager.isProvidedFor(MaterialService::class.java)) {
            servicesManager.register(MaterialService::class.java, this, plugin, ServicePriority.Normal)
            true
        } else false
    }
}
