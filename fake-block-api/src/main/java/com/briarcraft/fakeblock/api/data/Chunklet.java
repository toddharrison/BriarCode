package com.briarcraft.fakeblock.api.data;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.Data;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This data class represents a horizontal slice of a Chunk known as a Chunklet. Chunklets are the sets of block data
 * that are sent to the Minecraft client via multi-block change packets.
 */
@Data
public class Chunklet {
    private final @Nonnull BlockPosition position;
    private final @Nonnull World world;
    private final @Nonnull short[] relativeLocations;
    private final @Nonnull WrappedBlockData[] blockData;

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chunklet chunklet = (Chunklet) o;
        return position.equals(chunklet.position) && world.equals(chunklet.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, world);
    }
}
