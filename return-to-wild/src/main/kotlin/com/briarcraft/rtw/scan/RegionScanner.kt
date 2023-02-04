package com.briarcraft.rtw.scan

import com.briarcraft.kotlin.util.RegionLocation
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.World

// Usage:
//val regionScanner = RegionScanner()
//launch {
//    delay(15.seconds)
//    regionScanner.scanRegions(server.worlds.first { it.name == "briar" }, listOf(
//        RegionLocation(0,0),
//        RegionLocation(0,-1),
//        RegionLocation(-1,0),
//        RegionLocation(-1,-1),
//        RegionLocation(-2,1),
//        RegionLocation(1,-3),
//        RegionLocation(1,-2),
//        RegionLocation(1,-1),
//        RegionLocation(1,1),
//        RegionLocation(-2,11),
//        RegionLocation(-1,12),
//        RegionLocation(-1,11),
//        RegionLocation(-2,12),
//        RegionLocation(-11,-9),
//        RegionLocation(9,-8),
//    ))
//}

class RegionScanner {
    suspend fun scanRegions(world: World, regionLocations: List<RegionLocation>) {
        regionLocations.forEach { regionLoc ->
            val minChunkLoc = regionLoc.toMinChunkLocation()
            val maxChunkLoc = regionLoc.toMaxChunkLocation()
            (minChunkLoc.x..maxChunkLoc.x).forEach { chunkX ->
                (minChunkLoc.z..maxChunkLoc.z).forEach { chunkZ ->
                    val chunk = world.getChunkAt(chunkX, chunkZ)
                    println("Loaded Chunk ${chunk.x}, ${chunk.z}")
                    delay(1.ticks)
                }
            }
        }
    }
}
