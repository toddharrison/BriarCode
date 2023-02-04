package com.briarcraft.econ.material

import com.briarcraft.econ.api.material.MaterialSet
import org.bukkit.Material

class MaterialSetImpl(
    override val types: Set<Material>
): MaterialSet {
}
