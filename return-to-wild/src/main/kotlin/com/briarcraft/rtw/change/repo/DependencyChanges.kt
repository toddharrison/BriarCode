package com.briarcraft.rtw.change.repo

import org.bukkit.Location
import org.bukkit.World

data class DependencyChanges(
    val world: World,
    val locations: List<Location>
)