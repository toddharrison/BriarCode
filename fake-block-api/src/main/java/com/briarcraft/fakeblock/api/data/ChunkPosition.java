package com.briarcraft.fakeblock.api.data;

import lombok.Data;

/**
 * This data class represents a Chunk x and z location.
 */
@Data
public class ChunkPosition {
    private final int x;
    private final int z;
}
