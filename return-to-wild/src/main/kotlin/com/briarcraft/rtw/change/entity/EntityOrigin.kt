package com.briarcraft.rtw.change.entity

import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import java.time.Instant

data class EntityOrigin(
    val type: EntityType,
    val location: Location,
    val facing: BlockFace,
    val passengers: List<Entity>?,
    val loot: NamespacedKey?,
    val detail: String?,
    val timestamp: Instant = Instant.now()
)