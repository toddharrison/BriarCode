package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import com.briarcraft.fakeblock.api.event.HideFakeBlockGroupEvent;
import com.briarcraft.fakeblock.api.event.ShowFakeBlockGroupEvent;
import com.briarcraft.fakeblock.config.PlayerGroupConfig;
import com.briarcraft.fakeblock.data.PlayerGroup;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerGroupServiceImpl implements PlayerGroupService {
    private final @Nonnull PluginManager pluginManager;
    private final @Nonnull Function<UUID, Player> getPlayer;
    private final @Nonnull Map<UUID, Set<String>> playerGroups;

    private final @Nonnull GroupService groupService;
    private final @Nonnull ProtocolLibService protocolLibService;
    private final @Nonnull PlayerGroupConfig playerGroupConfig;

    public PlayerGroupServiceImpl(
            final @Nonnull PluginManager pluginManager,
            final @Nonnull GroupService groupService,
            final @Nonnull ProtocolLibService protocolLibService,
            final @Nonnull PlayerGroupConfig playerGroupConfig,
            final @Nonnull Function<UUID, Player> getPlayer,
            final @Nonnull Map<UUID, Set<String>> playerGroups
    ) {
        this.pluginManager = pluginManager;
        this.groupService = groupService;
        this.protocolLibService = protocolLibService;
        this.playerGroupConfig = playerGroupConfig;
        this.getPlayer = getPlayer;
        this.playerGroups = new HashMap<>(playerGroups);
    }

    @Override
    public boolean isVisible(final @Nonnull String groupName, final @Nonnull OfflinePlayer player) {
        return getVisibleGroups(player).contains(groupName);
    }

    @Override
    public @Nonnull Set<String> getVisibleGroups(final @Nonnull OfflinePlayer player) {
        val playerId = player.getUniqueId();
        return Set.copyOf(playerGroups.getOrDefault(playerId, Set.of()));
    }

    @Override
    public @Nonnull Set<UUID> getPlayersVisibleTo(final @Nonnull String groupName) {
        return playerGroups.entrySet().stream()
                .filter(entry -> entry.getValue().contains(groupName))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean showGroup(final @Nonnull String groupName, final @Nonnull OfflinePlayer player) {
        if (!Boolean.TRUE.equals(isGroupPresent(groupName))) return false;

        val event = new ShowFakeBlockGroupEvent(player, groupName);
        pluginManager.callEvent(event);
        if (!event.isCancelled()) {
            val playerId = player.getUniqueId();
            val groups = playerGroups.get(playerId);
            if (groups == null) {
                val newGroups = new HashSet<String>();
                newGroups.add(groupName);
                playerGroups.put(playerId, newGroups);
                playerGroupConfig.save(generatePlayerGroups());
                return true;
            } else {
                val added = groups.add(groupName);
                if (added) {
                    playerGroupConfig.save(generatePlayerGroups());
                }
                return added;
            }
        } else return false;
    }

    @Override
    public boolean showGroup(final @Nonnull String groupName, final @Nonnull Player player) {
        val isShown = showGroup(groupName, (OfflinePlayer) player);
        if (isShown) {
            groupService.getChunklets(groupName, player.getWorld())
                    .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet));
        }
        return isShown;
    }

    @Override
    public boolean showGroup(final @Nonnull String groupName, final @Nonnull UUID playerId) {
        val player = getPlayer.apply(playerId);
        if (player != null) {
            return showGroup(groupName, player);
        } else return false;
    }

    @Override
    public boolean hideGroup(final @Nonnull String groupName, final @Nonnull OfflinePlayer player) {
        if (!Boolean.TRUE.equals(isGroupPresent(groupName))) return false;

        val event = new HideFakeBlockGroupEvent(player, groupName);
        pluginManager.callEvent(event);
        if (!event.isCancelled()) {
            val playerId = player.getUniqueId();
            val groups = playerGroups.get(playerId);
            if (groups == null) {
                return false;
            } else {
                val isHidden = groups.remove(groupName);
                if (isHidden) {
                    if (player instanceof Player onlinePlayer) {
                        groupService.getChunklets(groupName, onlinePlayer.getWorld())
                                .forEach(chunklet -> protocolLibService.sendChunklet(onlinePlayer, chunklet, Material.AIR));
                    }
                    playerGroupConfig.save(generatePlayerGroups());
                }
                return isHidden;
            }
        } else return false;
    }

    @Override
    public boolean hideGroup(final @Nonnull String groupName, final @Nonnull Player player) {
        return hideGroup(groupName, (OfflinePlayer) player);
    }

    @Override
    public boolean hideGroup(final @Nonnull String groupName, final @Nonnull UUID playerId) {
        val player = getPlayer.apply(playerId);
        if (player != null) {
            return hideGroup(groupName, player);
        } else return false;
    }

    public @Nullable Boolean isGroupPresent(final @Nonnull String groupName) {
        return groupService.getGroupNames().contains(groupName);
    }

    private @Nonnull List<PlayerGroup> generatePlayerGroups() {
        return playerGroups.entrySet().stream()
                .map(entry -> new PlayerGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
