package com.briarcraft.fakeblock.listener;

import com.briarcraft.fakeblock.api.data.ChunkPosition;
import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import com.briarcraft.fakeblock.service.ProtocolLibService;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class FakeBlockPacketListener extends PacketAdapter {
    private final @Nonnull ProtocolLibService protocolLibService;
    private final @Nonnull GroupService groupService;
    private final @Nonnull PlayerGroupService playerGroupService;

    public FakeBlockPacketListener(
            final @Nonnull Plugin plugin,
            final @Nonnull ProtocolLibService protocolLibService,
            final @Nonnull GroupService groupService,
            final @Nonnull PlayerGroupService playerGroupService
    ) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.BLOCK_CHANGE, PacketType.Play.Server.MAP_CHUNK);
        this.protocolLibService = protocolLibService;
        this.groupService = groupService;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public void onPacketSending(final @Nonnull PacketEvent event) {
        if (event.isCancelled()) return;

        val packet = event.getPacket();
        val player = event.getPlayer();
        val world = player.getWorld();
        val packetType = event.getPacketType();

        if (packetType == PacketType.Play.Server.BLOCK_CHANGE) {
            val position = packet.getBlockPositionModifier().read(0);
            if (position != null) {
                val shownGroups = getShownGroups(player);
                groupService.getBlocks(shownGroups, world, position).values().stream()
                        .findFirst()
                        .ifPresent(block -> {
                            val wrappedBlockData = protocolLibService.wrapBlockData(block.getBlockData());
                            packet.getBlockData().write(0, wrappedBlockData);
                        });
            }
        } else if (packetType == PacketType.Play.Server.MAP_CHUNK) {
            val ints = packet.getIntegers();
            val chunkX = ints.read(0);
            val chunkZ = ints.read(1);
            val chunk = world.getChunkAt(chunkX, chunkZ);
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                while (!chunk.isLoaded()) {
                    Thread.yield();
                }
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    val shownGroups = getShownGroups(player);
                    groupService.getChunklets(shownGroups, world, new ChunkPosition(chunkX, chunkZ))
                            .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet));
                });
            });
        }
    }

    private @Nonnull Set<String> getShownGroups(final @Nonnull Player player) {
        val defaultGroups = groupService.getDefaultShownGroupNames();
        val playerGroups = playerGroupService.getConfiguredGroups(player);
        val shownGroups = new HashSet<>(defaultGroups);
        playerGroups.forEach((groupName, isShown) -> {
            if (isShown) {
                shownGroups.add(groupName);
            } else {
                shownGroups.remove(groupName);
            }
        });
        return shownGroups;
    }
}
