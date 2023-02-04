package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialSet
import com.briarcraft.kotlin.util.toEnumSet
import org.bukkit.Material

class ItemMaterials(invalidItems: MaterialSet, unavailableItems: MaterialSet): MaterialSet {
    override val types: Set<Material>

    init {
        types = Material.values()
            .filter(Material::isItem)
            .filterNot(Material::isLegacy)
            .filterNot(Material::isAir)
            .filterNot { invalidItems.types.contains(it) }
            .filterNot { unavailableItems.types.contains(it) }
            .toEnumSet()
    }
}