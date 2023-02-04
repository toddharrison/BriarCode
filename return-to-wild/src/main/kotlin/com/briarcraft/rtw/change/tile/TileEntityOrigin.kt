package com.briarcraft.rtw.change.tile

import org.bukkit.Location
import org.bukkit.Material
import java.time.Instant

data class TileEntityOrigin(
    val type: Material,
    val location: Location,
    val data: String,
    val detail: String?,
    val timestamp: Instant = Instant.now()
)