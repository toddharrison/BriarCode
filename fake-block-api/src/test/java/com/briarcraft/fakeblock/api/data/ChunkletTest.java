package com.briarcraft.fakeblock.api.data;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.val;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChunkletTest {
    private @Mock BlockPosition position;
    private @Mock World world;
    private final short[] relativeLocations = new short[0];
    private final WrappedBlockData[] blockData = new WrappedBlockData[0];

    private Chunklet chunklet;

    @BeforeEach
    public void setup() {
        chunklet = new Chunklet(position, world, relativeLocations, blockData);
    }

    @Nested
    public class EqualsTest {
        @Test
        public void callWithSame() {
            // Arrange

            // Act
            val response = chunklet.equals(chunklet);

            // Assert
            assertTrue(response);
        }

        @Test
        public void callWithNull() {
            // Arrange

            // Act
            val response = chunklet.equals(null);

            // Assert
            assertFalse(response);
        }
    }
}
