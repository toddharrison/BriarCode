package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.config.GroupConfig;
import com.briarcraft.fakeblock.api.data.ChunkPosition;
import com.briarcraft.fakeblock.api.data.Chunklet;
import com.briarcraft.fakeblock.api.data.FakeBlock;
import com.briarcraft.fakeblock.api.data.Group;
import com.briarcraft.fakeblock.api.event.CreateFakeBlockGroupEvent;
import com.briarcraft.fakeblock.api.event.DeleteFakeBlockGroupEvent;
import com.briarcraft.fakeblock.api.event.UpdateFakeBlockGroupEvent;
import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.val;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class GroupServiceImpl implements GroupService {
    private final @Nonnull PluginManager pluginManager;
    private final @Nonnull ServicesManager servicesManager;
    private final @Nonnull ChunkService chunkService;
    private final @Nonnull GroupConfig groupConfig;
    private final @Nonnull Map<String, Group> groups;
    private final @Nonnull Map<World, Map<ChunkPosition, Map<String, List<Chunklet>>>> chunklets = new HashMap<>();
    private final @Nonnull Map<World, Map<BlockPosition, Map<String, FakeBlock>>> fakeBlocks = new HashMap<>();

    public GroupServiceImpl(
            final @Nonnull PluginManager pluginManager,
            final @Nonnull ServicesManager servicesManager,
            final @Nonnull ChunkService chunkService,
            final @Nonnull GroupConfig groupConfig,
            final @Nonnull Map<String, Group> groups
    ) {
        this.pluginManager = pluginManager;
        this.servicesManager = servicesManager;
        this.chunkService = chunkService;
        this.groupConfig = groupConfig;
        this.groups = new HashMap<>(groups);

        this.groups.values().forEach(this::updateChunklets);
        this.groups.values().forEach(this::updateFakeBlocks);
    }

    @Override
    public @Nonnull Set<String> getGroupNames() {
        return Set.copyOf(groups.keySet());
    }

    @Override
    public boolean hasGroup(final @Nonnull String groupName) {
        return groups.containsKey(groupName);
    }

    @Override
    public @Nullable World getWorld(final @Nonnull String groupName) {
        val group = groups.get(groupName);
        if (group != null) {
            return group.getWorld();
        } else return null;
    }

    @Override
    public boolean create(final @Nonnull String groupName, final @Nonnull World world, final @Nonnull Set<FakeBlock> fakeBlocks) {
        if (groups.containsKey(groupName)) return false;

        val group = new Group(groupName, world, new HashSet<>(fakeBlocks));
        val event = new CreateFakeBlockGroupEvent(group);
        pluginManager.callEvent(event);
        if (!event.isCancelled()) {
            groups.put(groupName, group);
            setChunklets(group);
            setFakeBlocks(group);
            groupConfig.save(getGroups());
            return true;
        } else return false;
    }

    @Override
    public boolean addBlocks(final @Nonnull String groupName, final @Nonnull Set<FakeBlock> fakeBlocks) {
        val group = groups.get(groupName);
        if (group != null) {
            val event = new UpdateFakeBlockGroupEvent(group, fakeBlocks, Set.of());
            pluginManager.callEvent(event);
            if (!event.isCancelled()) {
                val groupBlocks = group.getFakeBlocks();
                groupBlocks.removeAll(fakeBlocks);
                val added = groupBlocks.addAll(fakeBlocks);
                if (added) {
                    updateChunklets(group);
                    updateFakeBlocks(group);
                    groupConfig.save(getGroups());
                }
                return added;
            } else return false;
        } else return false;
    }

    @Override
    public @Nonnull Set<FakeBlock> removeBlocks(final @Nonnull String groupName, final @Nonnull Set<BlockPosition> positions) {
        val group = groups.get(groupName);
        if (group != null) {
            val event = new UpdateFakeBlockGroupEvent(group, Set.of(), positions);
            pluginManager.callEvent(event);
            if (!event.isCancelled()) {
                val groupBlocks = group.getFakeBlocks();
                val removedBlocks = groupBlocks.stream()
                        .filter(block -> positions.contains(block.getPosition()))
                        .collect(Collectors.toSet());
                val removed = groupBlocks.removeAll(removedBlocks);
                if (removed) {
                    updateChunklets(group);
                    updateFakeBlocks(group);
                    groupConfig.save(getGroups());
                }
                return removedBlocks;
            } else return Set.of();
        } else return Set.of();
    }

    @Override
    public @Nonnull Set<FakeBlock> delete(final @Nonnull String groupName) {
        if (!groups.containsKey(groupName)) return Set.of();

        val serviceProvider = servicesManager.getRegistration(PlayerGroupService.class);
        if (serviceProvider != null) {
            val group = groups.get(groupName);
            val event = new DeleteFakeBlockGroupEvent(group);
            pluginManager.callEvent(event);
            if (!event.isCancelled()) {
                // Remove players from group being deleted
                val playerGroupService = serviceProvider.getProvider();
                playerGroupService.getPlayersVisibleTo(groupName)
                        .forEach(playerId -> playerGroupService.hideGroup(groupName, playerId));

                deleteChunklets(groupName);
                deleteFakeBlocks(groupName);
                val removedGroup = groups.remove(groupName);
                groupConfig.save(getGroups());
                return removedGroup.getFakeBlocks();
            } else return Set.of();
        } else return Set.of();
    }

    @Override
    public @Nonnull Set<FakeBlock> getBlocks(final @Nonnull String groupName) {
        val group = groups.get(groupName);
        if (group != null) {
            return Set.copyOf(group.getFakeBlocks());
        } else return Set.of();
    }

    @Override
    public @Nonnull Map<String, FakeBlock> getBlocks(final @Nonnull Set<String> groups, final @Nonnull World world, final @Nonnull BlockPosition blockPosition) {
        val worldBlocks = fakeBlocks.get(world);
        if (worldBlocks != null) {
            val blocks = worldBlocks.get(blockPosition);
            if (blocks != null) {
                return blocks;
            }
        }
        return Map.of();
    }

    @Override
    public @Nonnull List<Chunklet> getChunklets(final @Nonnull Set<String> groups, final @Nonnull World world, final @Nonnull ChunkPosition chunkPosition) {
        val worldChunklets = chunklets.get(world);
        if (worldChunklets != null) {
            val groupChunklets = worldChunklets.get(chunkPosition);
            if (groupChunklets != null) {
                return groupChunklets.entrySet().stream()
                        .filter(entry -> groups.contains(entry.getKey()))
                        .flatMap(entry -> entry.getValue().stream())
                        .toList();
            }
        }
        return List.of();
    }

    @Override
    public @Nonnull List<Chunklet> getChunklets(final @Nonnull String groupName, final @Nonnull World world) {
        val worldChunklets = chunklets.get(world);
        if (worldChunklets != null) {
            return worldChunklets.values().stream()
                    .flatMap(entry -> entry.getOrDefault(groupName, List.of()).stream())
                    .toList();
        } else return List.of();
    }

    private @Nonnull Collection<Group> getGroups() {
        return groups.values();
    }

    private void updateChunklets(final @Nonnull Group group) {
        deleteChunklets(group.getName());
        setChunklets(group);
    }

    private void setChunklets(final @Nonnull Group group) {
        chunkService.toChunklets(group).stream()
                .collect(Collectors.groupingBy(chunklet -> chunkService.toChunk(chunklet.getPosition())))
                .forEach((chunkPosition, chunkChunklets) -> {
                    val worldChunklets = chunklets.getOrDefault(group.getWorld(), new HashMap<>());
                    val groupChunklets = worldChunklets.getOrDefault(chunkPosition, new HashMap<>());
                    groupChunklets.put(group.getName(), chunkChunklets);
                    worldChunklets.put(chunkPosition, groupChunklets);
                    chunklets.put(group.getWorld(), worldChunklets);
                });
    }

    private void deleteChunklets(final @Nonnull String groupName) {
        chunklets.forEach((world, worldGroups) -> worldGroups.forEach((chunkPosition, chunkGroups) -> chunkGroups.remove(groupName)));
    }

    private void updateFakeBlocks(final @Nonnull Group group) {
        deleteFakeBlocks(group.getName());
        setFakeBlocks(group);
    }

    private void setFakeBlocks(final @Nonnull Group group) {
        val world = group.getWorld();
        val worldPositions = fakeBlocks.getOrDefault(world, new HashMap<>());
        group.getFakeBlocks().forEach(fakeBlock -> {
            val position = fakeBlock.getPosition();
            val groups = worldPositions.getOrDefault(position, new HashMap<>());
            groups.put(group.getName(), fakeBlock);
            worldPositions.put(fakeBlock.getPosition(), groups);
        });
        fakeBlocks.put(world, worldPositions);
    }

    private void deleteFakeBlocks(final @Nonnull String groupName) {
        fakeBlocks.forEach((world, worldPositions) -> worldPositions.forEach((groups, fakeBlocks) -> fakeBlocks.remove(groupName)));
    }
}
