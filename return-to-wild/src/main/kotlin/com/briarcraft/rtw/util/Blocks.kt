package com.briarcraft.rtw.util

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

fun getImmediatelyAdjacentBlockLocations(location: Location) =
    listOf(
        Location(location.world, location.x + 1, location.y, location.z),
        Location(location.world, location.x, location.y + 1, location.z),
        Location(location.world, location.x, location.y, location.z + 1),
        Location(location.world, location.x - 1, location.y, location.z),
        Location(location.world, location.x, location.y - 1, location.z),
        Location(location.world, location.x, location.y, location.z - 1)
    )

fun getAdjacentTriggeringBlocks(block: Block) =
    setOf(
        block.getRelative(0, -1, 0),

        block.getRelative(-1, 0, 0),
        block.getRelative(1, 0, 0),
        block.getRelative(0, 0, -1),
        block.getRelative(0, 0, 1),

        block.getRelative(0, 1, 0),
        block.getRelative(-1, 1, 0),
        block.getRelative(1, 1, 0),
        block.getRelative(0, 1, -1),
        block.getRelative(0, 1, 1)
    )

fun getImmediatelyAdjacentBlocks(block: Block) =
    setOf(
        block.getRelative(BlockFace.NORTH),
        block.getRelative(BlockFace.SOUTH),
        block.getRelative(BlockFace.EAST),
        block.getRelative(BlockFace.WEST),
        block.getRelative(BlockFace.UP),
        block.getRelative(BlockFace.DOWN)
    )

fun getHorizontalAdjacentBlockLocations(location: Location): List<Location> {
    return listOf(
        Location(location.world, location.x + 1, location.y, location.z),
        Location(location.world, location.x, location.y, location.z + 1),
        Location(location.world, location.x - 1, location.y, location.z),
        Location(location.world, location.x, location.y, location.z - 1)
    )
}

fun findBlockAbove(location: Location, type: Material, distance: Int): Location {
    var testBlock = location.block.getRelative(BlockFace.UP)
    var distanceLeft = distance
    while (testBlock.type != type && distanceLeft-- > 0) {
        testBlock = testBlock.getRelative(BlockFace.UP)
    }
    return testBlock.location
}
