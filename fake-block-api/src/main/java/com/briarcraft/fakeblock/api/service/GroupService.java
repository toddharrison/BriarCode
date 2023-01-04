package com.briarcraft.fakeblock.api.service;

import com.briarcraft.fakeblock.api.data.ChunkPosition;
import com.briarcraft.fakeblock.api.data.Chunklet;
import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This service manages the Groups that have been defined in fake-block.
 */
public interface GroupService {
    /**
     * Get the Set of all Group names in this service.
     * @return The Set of Group names.
     */
    @Nonnull Set<String> getGroupNames();

    /**
     * Get the Set of all Group names in this service that are shown by default.
     * @return The Set of Group names shown by default.
     */
    @Nonnull Set<String> getDefaultShownGroupNames();

    /**
     * Determine if this service has a Group by the specified name.
     * @param groupName The name of the Group.
     * @return True if this service has the Group, false otherwise.
     */
    boolean hasGroup(@Nonnull String groupName);

    /**
     * Determine if this service has a Group by the specified name and if it is shown by default.
     * @param groupName The name of the Group.
     * @return True if this service has the Group and it is shown by default, false otherwise.
     */
    boolean isGroupShownByDefault(@Nonnull String groupName);

    /**
     * Get the World that the specified Group exists within.
     * @param groupName The name of the Group.
     * @return The World if available, null otherwise.
     */
    @Nullable World getWorld(@Nonnull String groupName);

    /**
     * Create a new Group using the specified blocks with the specified Group name, in the specified World.
     * @param groupName The name for the new Group.
     * @param world The World the Group exists in.
     * @param fakeBlocks The Set of FakeBlocks making up the Group.
     * @param isGroupShownByDefault If the Group should be shown by default.
     * @return True if creating the Group succeeded, false otherwise.
     */
    boolean create(@Nonnull String groupName, @Nonnull World world, @Nonnull Set<FakeBlock> fakeBlocks, boolean isGroupShownByDefault);

    /**
     * Add the specified blocks in the specified World to an existing Group in this service.
     * @param groupName The name for the existing Group.
     * @param fakeBlocks The Set of FakeBlocks to add to the Group.
     * @return True if adding the blocks to the Group succeeded, false otherwise.
     */
    boolean addBlocks(@Nonnull String groupName, @Nonnull Set<FakeBlock> fakeBlocks);

    /**
     * Remove the specified BlockPositions from the specified Group name.
     * @param groupName The name of the Group.
     * @param positions The BlockPositions to remove from the Group.
     * @return A Set of the FakeBlocks removed, empty if none were removed.
     */
    @Nonnull Set<FakeBlock> removeBlocks(@Nonnull String groupName, @Nonnull Set<BlockPosition> positions);

    /**
     * Delete the specified Group from this service.
     * @param groupName The name of the Group to delete.
     * @return The Set of FakeBlocks associated with the deleted Group.
     */
    @Nonnull Set<FakeBlock> delete(@Nonnull String groupName);

    /**
     * Get the FakeBlocks associated with the specified Group name from this service.
     * @param groupName The name of the Group.
     * @return The Set of FakeBlocks associated with the Group, empty if there is no Group.
     */
    @Nonnull Set<FakeBlock> getBlocks(@Nonnull String groupName);

    /**
     * Get the FakeBlocks at the specified BlockPosition in the specified World.
     * @param groups The Group names to retrieve.
     * @param world The World the Groups exist in.
     * @param blockPosition The BlockPosition to retrieve.
     * @return A Map of Group names to FakeBlocks at the specified BlockPosition in the specified World.
     */
    @Nonnull Map<String, FakeBlock> getBlocks(@Nonnull Set<String> groups, @Nonnull World world, @Nonnull BlockPosition blockPosition);

    /**
     * Get the Chunklets at the specified ChunkPosition in the specified World.
     * @param groups The Group names to retrieve.
     * @param world The World the Groups exist in.
     * @param chunkPosition The ChunkPosition to retrieve.
     * @return A List of the Chunklets for the specified ChunkPosition in the specified World.
     */
    @Nonnull List<Chunklet> getChunklets(@Nonnull Set<String> groups, @Nonnull World world, @Nonnull ChunkPosition chunkPosition);

    /**
     * Get the Chunklets for a specified Group name in the specified World.
     * @param groupName The Group to retrieve Chunklets for.
     * @param world The World the Group exists in.
     * @return A List of the Chunklets belonging to the specified Group name in the specified World.
     */
    @Nonnull List<Chunklet> getChunklets(@Nonnull String groupName, @Nonnull World world);
}
