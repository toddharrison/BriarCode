package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.api.data.ChunkPosition;
import com.briarcraft.fakeblock.api.data.Chunklet;
import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.briarcraft.fakeblock.api.data.Group;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.val;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChunkService {
    public @Nonnull Set<Chunklet> toChunklets(final @Nonnull Group group) {
        return group.getFakeBlocks().stream()
                .collect(Collectors.groupingBy(fakeBlock -> toChunklet(fakeBlock.getPosition())))
                .entrySet().stream()
                .map(entry -> new Chunklet(entry.getKey(), group.getWorld(), asLocShorts(entry.getValue()), asWrappedBlockData(entry.getValue())))
                .collect(Collectors.toSet());
    }

    public @Nonnull ChunkPosition toChunk(final @Nonnull BlockPosition chunkletPosition) {
        return new ChunkPosition(chunkletPosition.getX(), chunkletPosition.getZ());
    }

    public @Nonnull BlockPosition toChunklet(final @Nonnull BlockPosition blockPosition) {
        return new BlockPosition(blockPosition.getX() >> 4, blockPosition.getY() >> 4, blockPosition.getZ() >> 4);
    }

    public short toChunkletRelativeLocationAsShort(final @Nonnull BlockPosition position) {
        return (short) ((position.getX() & 15) << 8 | (position.getZ() & 15) << 4 | (position.getY() & 15));
    }

    private short[] asLocShorts(final @Nonnull List<FakeBlock> fakeBlocks) {
        val locAsShorts = fakeBlocks.stream()
                .map(FakeBlock::getPosition)
                .map(this::toChunkletRelativeLocationAsShort)
                .toArray(Short[]::new);
        short[] shortArray = new short[locAsShorts.length];
        for (int i = 0; i < locAsShorts.length; i++) shortArray[i] = locAsShorts[i];
        return shortArray;
    }

    private WrappedBlockData[] asWrappedBlockData(final @Nonnull List<FakeBlock> fakeBlocks) {
        return fakeBlocks.stream()
                .map(FakeBlock::getBlockData)
                .map(WrappedBlockData::createData)
                .toArray(WrappedBlockData[]::new);
    }
}
