package com.briarcraft.rtw.change.block

import com.briarcraft.rtw.category.materialCategoryService
import com.briarcraft.rtw.util.Locatable
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.data.BlockData
import java.time.Instant

data class BlockChange(
    val context: String,
    val type: String,
    val cause: NamespacedKey?,
    val causeName: String?,
    override val location: Location,
    val blockData: BlockData,
    val category: Int = materialCategoryService.getCategory(blockData.material),
    val newMaterial: Material,
    val newCategory: Int = materialCategoryService.getCategory(newMaterial),
    val timestamp: Instant = Instant.now()
): Locatable
