package com.briarcraft.rtw.change.player

import org.bukkit.Location
import java.time.Instant
import java.util.*

data class PlayerLogoff(
    val name: String,
    val id: UUID,
    val location: Location,
    val timestamp: Instant = Instant.now()
)