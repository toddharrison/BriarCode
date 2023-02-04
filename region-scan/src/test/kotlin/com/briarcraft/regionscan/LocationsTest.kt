package com.briarcraft.regionscan

import com.briarcraft.kotlin.util.BlockLocation
import com.briarcraft.kotlin.util.ChunkLocation
import com.briarcraft.kotlin.util.RegionLocation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LocationsTest {
    @Test
    fun `block locations`() {
        var loc = BlockLocation(0, 0)
        assertEquals(ChunkLocation(0, 0), loc.toChunkLocation())
        assertEquals(RegionLocation(0, 0), loc.toRegionLocation())

        loc = BlockLocation(15, 15)
        assertEquals(ChunkLocation(0, 0), loc.toChunkLocation())
        assertEquals(RegionLocation(0, 0), loc.toRegionLocation())

        loc = BlockLocation(16, 16)
        assertEquals(ChunkLocation(1, 1), loc.toChunkLocation())
        assertEquals(RegionLocation(0, 0), loc.toRegionLocation())

        loc = BlockLocation(-1, -1)
        assertEquals(ChunkLocation(-1, -1), loc.toChunkLocation())
        assertEquals(RegionLocation(-1, -1), loc.toRegionLocation())

        loc = BlockLocation(-16, -16)
        assertEquals(ChunkLocation(-1, -1), loc.toChunkLocation())
        assertEquals(RegionLocation(-1, -1), loc.toRegionLocation())

        loc = BlockLocation(-17, -17)
        assertEquals(ChunkLocation(-2, -2), loc.toChunkLocation())
        assertEquals(RegionLocation(-1, -1), loc.toRegionLocation())

        loc = BlockLocation(511, 511)
        assertEquals(ChunkLocation(31, 31), loc.toChunkLocation())
        assertEquals(RegionLocation(0, 0), loc.toRegionLocation())

        loc = BlockLocation(512, 512)
        assertEquals(ChunkLocation(32, 32), loc.toChunkLocation())
        assertEquals(RegionLocation(1, 1), loc.toRegionLocation())

        loc = BlockLocation(-512, -512)
        assertEquals(ChunkLocation(-32, -32), loc.toChunkLocation())
        assertEquals(RegionLocation(-1, -1), loc.toRegionLocation())

        loc = BlockLocation(-513, -513)
        assertEquals(ChunkLocation(-33, -33), loc.toChunkLocation())
        assertEquals(RegionLocation(-2, -2), loc.toRegionLocation())
    }

    @Test
    fun `chunk to block min and max`() {
        var loc = ChunkLocation(0, 0)
        assertEquals(BlockLocation(0, 0), loc.toMinBlockLocation())
        assertEquals(BlockLocation(15, 15), loc.toMaxBlockLocation())

        loc = ChunkLocation(1, 1)
        assertEquals(BlockLocation(16, 16), loc.toMinBlockLocation())
        assertEquals(BlockLocation(31, 31), loc.toMaxBlockLocation())

        loc = ChunkLocation(-1, -1)
        assertEquals(BlockLocation(-16, -16), loc.toMinBlockLocation())
        assertEquals(BlockLocation(-1, -1), loc.toMaxBlockLocation())

        loc = ChunkLocation(-2, -2)
        assertEquals(BlockLocation(-32, -32), loc.toMinBlockLocation())
        assertEquals(BlockLocation(-17, -17), loc.toMaxBlockLocation())
    }

    @Test
    fun `chunk locations`() {
        var loc = ChunkLocation(0, 0)
        assertEquals(RegionLocation(0, 0), loc.toRegionLocation())

        loc = ChunkLocation(31, 31)
        assertEquals(RegionLocation(0, 0), loc.toRegionLocation())

        loc = ChunkLocation(32, 32)
        assertEquals(RegionLocation(1, 1), loc.toRegionLocation())

        loc = ChunkLocation(-1, -1)
        assertEquals(RegionLocation(-1, -1), loc.toRegionLocation())

        loc = ChunkLocation(-32, -32)
        assertEquals(RegionLocation(-1, -1), loc.toRegionLocation())

        loc = ChunkLocation(-33, -33)
        assertEquals(RegionLocation(-2, -2), loc.toRegionLocation())
    }

    @Test
    fun `region to block and chunk min and max`() {
        var loc = RegionLocation(0, 0)
        assertEquals(BlockLocation(0, 0), loc.toMinBlockLocation())
        assertEquals(BlockLocation(511, 511), loc.toMaxBlockLocation())
        assertEquals(ChunkLocation(0, 0), loc.toMinChunkLocation())
        assertEquals(ChunkLocation(31, 31), loc.toMaxChunkLocation())

        loc = RegionLocation(1, 1)
        assertEquals(BlockLocation(512, 512), loc.toMinBlockLocation())
        assertEquals(BlockLocation(1023, 1023), loc.toMaxBlockLocation())
        assertEquals(ChunkLocation(32, 32), loc.toMinChunkLocation())
        assertEquals(ChunkLocation(63, 63), loc.toMaxChunkLocation())

        loc = RegionLocation(-1, -1)
        assertEquals(BlockLocation(-512, -512), loc.toMinBlockLocation())
        assertEquals(BlockLocation(-1, -1), loc.toMaxBlockLocation())
        assertEquals(ChunkLocation(-32, -32), loc.toMinChunkLocation())
        assertEquals(ChunkLocation(-1, -1), loc.toMaxChunkLocation())
    }
}
