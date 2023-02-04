package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialSet
import org.bukkit.Material

class CobbleReduceMaterials(
    override val types: Set<Material> = setOf(
        Material.COBBLESTONE,
        Material.COBBLED_DEEPSLATE,
        Material.BLACKSTONE,
    )
): MaterialSet {
}