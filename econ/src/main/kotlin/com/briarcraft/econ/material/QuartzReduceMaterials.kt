package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialSet
import org.bukkit.Material

class QuartzReduceMaterials(
    override val types: Set<Material> = setOf(
        Material.CHISELED_QUARTZ_BLOCK,
        Material.QUARTZ_BLOCK,
        Material.QUARTZ_PILLAR,
    )
): MaterialSet {
}