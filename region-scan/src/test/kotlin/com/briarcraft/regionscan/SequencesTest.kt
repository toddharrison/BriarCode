package com.briarcraft.regionscan

import com.briarcraft.kotlin.util.ChunkLocation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SequencesTest {
    @Test
    fun `generate spiral sequence values`() {
        // Act
        val sequence = getSpiralSequence().iterator()

        // Assert
        assertEquals(ChunkLocation(0, 0), sequence.next())
        assertEquals(ChunkLocation(1, 0), sequence.next())
        assertEquals(ChunkLocation(1, 1), sequence.next())
        assertEquals(ChunkLocation(0, 1), sequence.next())
        assertEquals(ChunkLocation(-1, 1), sequence.next())
        assertEquals(ChunkLocation(-1, 0), sequence.next())
        assertEquals(ChunkLocation(-1, -1), sequence.next())
        assertEquals(ChunkLocation(0, -1), sequence.next())
        assertEquals(ChunkLocation(1, -1), sequence.next())

        assertEquals(ChunkLocation(2, -1), sequence.next())
        assertEquals(ChunkLocation(2, 0), sequence.next())
        assertEquals(ChunkLocation(2, 1), sequence.next())
        assertEquals(ChunkLocation(2, 2), sequence.next())
        assertEquals(ChunkLocation(1, 2), sequence.next())
        assertEquals(ChunkLocation(0, 2), sequence.next())
        assertEquals(ChunkLocation(-1, 2), sequence.next())
        assertEquals(ChunkLocation(-2, 2), sequence.next())
        assertEquals(ChunkLocation(-2, 1), sequence.next())
        assertEquals(ChunkLocation(-2, 0), sequence.next())
        assertEquals(ChunkLocation(-2, -1), sequence.next())
        assertEquals(ChunkLocation(-2, -2), sequence.next())
        assertEquals(ChunkLocation(-1, -2), sequence.next())
        assertEquals(ChunkLocation(0, -2), sequence.next())
        assertEquals(ChunkLocation(1, -2), sequence.next())
        assertEquals(ChunkLocation(2, -2), sequence.next())
    }
}
