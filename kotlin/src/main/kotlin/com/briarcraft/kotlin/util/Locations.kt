package com.briarcraft.kotlin.util

import org.bukkit.Location

data class BlockLocation(
    val x: Int,
    val z: Int,
    val y: Int = 0
) {
    fun toChunkLocation() = ChunkLocation(x shr 4, z shr 4)
    fun toChunkletLocation() = ChunkletLocation(x shr 4, z shr 4, y shr 4)
    fun toRegionLocation() = RegionLocation(x shr 9, z shr 9)

    fun toBlockKey() = x.toLong() and 0x7FFFFFF or (z.toLong() and 0x7FFFFFF shl 27) or (y.toLong() shl 54)
    fun toChunkKey() = (x shr 4).toLong() and 0xffffffffL or ((z shr 4).toLong() and 0xffffffffL) shl 32
    fun toChunkletKey() = (x shr 4).toLong() and 0x7FFFFFF or ((z shr 4).toLong() and 0x7FFFFFF shl 27) or ((y shr 4).toLong() shl 54)
    fun toRegionKey() = (x shr 9).toLong() and 0xffffffffL or ((z shr 9).toLong() and 0xffffffffL) shl 32
}

fun fromBlockKey(key: Long) = BlockLocation(
    ((key shl 37) shr 37).toInt(),
    (key shr 54).toInt(),
    ((key shl 10) shr 37).toInt()
)

data class ChunkLocation(
    val x: Int,
    val z: Int
) {
    fun toRegionLocation() = RegionLocation(x shr 5, z shr 5)
    fun toMinBlockLocation() = BlockLocation(x shl 4, z shl 4)
    fun toMaxBlockLocation() = BlockLocation((x shl 4) + 15, (z shl 4) + 15)

    fun toChunkKey() = x.toLong() and 0xffffffffL or (z.toLong() and 0xffffffffL) shl 32
}

fun fromChunkKey(key: Long) = ChunkLocation(
    key.toInt(),
    (key shr 32).toInt()
)

data class ChunkletLocation(
    val x: Int,
    val z: Int,
    val y: Int
) {
    fun toRegionLocation() = RegionLocation(x shr 5, z shr 5)
    fun toMinBlockLocation() = BlockLocation(x shl 4, z shl 4, y shl 4)
    fun toMaxBlockLocation() = BlockLocation((x shl 4) + 15, (z shl 4) + 15, (y shl 4) + 15)

    fun toChunkLocation() = ChunkLocation(x, z)

    fun toChunkletKey() = x.toLong() and 0x7FFFFFF or (z.toLong() and 0x7FFFFFF shl 27) or (y.toLong() shl 54)
}

fun fromChunkletKey(key: Long) = ChunkletLocation(
    ((key shl 37) shr 37).toInt(),
    (key shr 54).toInt(),
    ((key shl 10) shr 37).toInt()
)

data class RegionLocation(
    val x: Int,
    val z: Int
) {
    fun toMinBlockLocation() = BlockLocation(x shl 9, z shl 9)
    fun toMaxBlockLocation() = BlockLocation((x shl 9) + 511, (z shl 9) + 511)
    fun toMinChunkLocation() = ChunkLocation(x shl 5, z shl 5)
    fun toMaxChunkLocation() = ChunkLocation((x shl 5) + 31, (z shl 5) + 31)

    fun toRegionKey() = x.toLong() and 0xffffffffL or (z.toLong() and 0xffffffffL) shl 32
}

fun fromRegionKey(key: Long) = RegionLocation(
    key.toInt(),
    (key shr 32).toInt()
)

fun Location.asBlockLocation() = BlockLocation(blockX, blockZ, blockY)
fun Location.asChunkLocation() = asBlockLocation().toChunkLocation()
fun Location.asChunkletLocation() = asBlockLocation().toChunkletLocation()
fun Location.asRegionLocation() = asBlockLocation().toRegionLocation()
