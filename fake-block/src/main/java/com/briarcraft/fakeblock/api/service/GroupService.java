package com.briarcraft.fakeblock.api.service;

import com.briarcraft.fakeblock.api.data.ChunkPosition;
import com.briarcraft.fakeblock.api.data.Chunklet;
import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public interface GroupService {
    @Nonnull Set<String> getGroupNames();
    boolean hasGroup(@Nonnull String groupName);
    @Nullable World getWorld(@Nonnull String groupName);
    boolean create(@Nonnull String groupName, @Nonnull World world, @Nonnull Set<FakeBlock> fakeBlocks);
    boolean addBlocks(@Nonnull String groupName, @Nonnull Set<FakeBlock> fakeBlocks);
    @Nonnull Set<FakeBlock> removeBlocks(@Nonnull String groupName, @Nonnull Set<BlockPosition> positions);
    @Nonnull Set<FakeBlock> delete(@Nonnull String groupName);

    @Nonnull Set<FakeBlock> getBlocks(@Nonnull String groupName);
    @Nonnull List<Chunklet> getChunklets(@Nonnull Set<String> groups, @Nonnull World world, @Nonnull ChunkPosition chunkPosition);
    @Nonnull List<Chunklet> getChunklets(@Nonnull String groupName, @Nonnull World world);
}
