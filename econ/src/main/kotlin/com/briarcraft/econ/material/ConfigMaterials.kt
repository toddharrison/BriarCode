package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialSet
import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material
import org.bukkit.configuration.Configuration

class ConfigMaterials(
    config: Configuration,
    val name: String
): MaterialSet {
    override val types: Set<Material> = config.getStringList("$name.types")
        .mapNotNull(Material::getMaterial)
        .let {
            if (it.count() < 16) {
                it.toSet()
            } else it.toEnumSet()
        }
}
