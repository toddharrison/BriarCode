package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.api.event.ClearFakeBlockGroupEvent;
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
    private final @Nonnull Function<UUID, OfflinePlayer> getPlayer;
    private final @Nonnull Map<UUID, Map<String, Boolean>> playerGroups;

    private final @Nonnull GroupService groupService;
    private final @Nonnull ProtocolLibService protocolLibService;
    private final @Nonnull PlayerGroupConfig playerGroupConfig;

    public PlayerGroupServiceImpl(
            final @Nonnull PluginManager pluginManager,
            final @Nonnull GroupService groupService,
            final @Nonnull ProtocolLibService protocolLibService,
            final @Nonnull PlayerGroupConfig playerGroupConfig,
            final @Nonnull Function<UUID, OfflinePlayer> getPlayer,
            final @Nonnull Map<UUID, Map<String, Boolean>> playerGroups
    ) {
        this.pluginManager = pluginManager;
        this.groupService = groupService;
        this.protocolLibService = protocolLibService;
        this.playerGroupConfig = playerGroupConfig;
        this.getPlayer = getPlayer;
        this.playerGroups = new HashMap<>();
        playerGroups.forEach((playerId, groups) -> this.playerGroups.put(playerId, new HashMap<>(groups)));
    }

    @Override
    public boolean isShown(final @Nonnull String groupName, final @Nonnull OfflinePlayer player) {
        val playerGroups = getConfiguredGroups(player);
        return (playerGroups.containsKey(groupName) && playerGroups.get(groupName))
                || groupService.isGroupShownByDefault(groupName);
    }

    @Override
    public boolean isHidden(final @Nonnull String groupName, final @Nonnull OfflinePlayer player) {
        val playerGroups = getConfiguredGroups(player);
        return (playerGroups.containsKey(groupName) && !playerGroups.get(groupName))
                || !groupService.isGroupShownByDefault(groupName);
    }

    @Override
    public @Nonnull Map<String, Boolean> getConfiguredGroups(final @Nonnull OfflinePlayer player) {
        val playerId = player.getUniqueId();
        return Map.copyOf(playerGroups.getOrDefault(playerId, Map.of()));
    }

    @Override
    public @Nonnull Set<UUID> getPlayersConfiguredIn(final @Nonnull String groupName) {
        return playerGroups.entrySet().stream()
                .filter(entry -> entry.getValue().containsKey(groupName))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean showGroup(final @Nonnull String groupName, final @Nonnull OfflinePlayer offlinePlayer) {
        if (!Boolean.TRUE.equals(isGroupPresent(groupName))) return false;

        val event = new ShowFakeBlockGroupEvent(offlinePlayer, groupName);
        pluginManager.callEvent(event);
        if (!event.isCancelled()) {
            val playerId = offlinePlayer.getUniqueId();
            val groups = playerGroups.get(playerId);
            if (groups == null) {
                val newGroups = new HashMap<String, Boolean>();
                newGroups.put(groupName, true);
                playerGroups.put(playerId, newGroups);
                playerGroupConfig.save(generatePlayerGroups());
                return true;
            } else {
                var isShown = false;
                if (!groups.containsKey(groupName) || !groups.get(groupName)) {
                    groups.put(groupName, true);
                    playerGroupConfig.save(generatePlayerGroups());
                    isShown = true;

                    if (offlinePlayer instanceof Player player) {
                        groupService.getChunklets(groupName, player.getWorld())
                                .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet));
                    }
                }
                return isShown;
            }
        } else return false;
    }

    @Override
    public boolean showGroup(final @Nonnull String groupName, final @Nonnull UUID playerId) {
        val offlinePlayer = getPlayer.apply(playerId);
        if (offlinePlayer != null) {
            return showGroup(groupName, offlinePlayer);
        } else return false;
    }

    @Override
    public boolean hideGroup(final @Nonnull String groupName, final @Nonnull OfflinePlayer offlinePlayer) {
        if (!Boolean.TRUE.equals(isGroupPresent(groupName))) return false;

        val event = new HideFakeBlockGroupEvent(offlinePlayer, groupName);
        pluginManager.callEvent(event);
        if (!event.isCancelled()) {
            val playerId = offlinePlayer.getUniqueId();
            val groups = playerGroups.get(playerId);
            if (groups == null) {
                val newGroups = new HashMap<String, Boolean>();
                newGroups.put(groupName, false);
                playerGroups.put(playerId, newGroups);
                playerGroupConfig.save(generatePlayerGroups());
                return true;
            } else {
                var isHidden = false;
                if (!groups.containsKey(groupName) || groups.get(groupName)) {
                    groups.put(groupName, false);
                    playerGroupConfig.save(generatePlayerGroups());
                    isHidden = true;

                    if (offlinePlayer instanceof Player player) {
                        groupService.getChunklets(groupName, player.getWorld())
                                .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet, Material.AIR));
                    }
                }
                return isHidden;
            }
        } else return false;
    }

    @Override
    public boolean hideGroup(final @Nonnull String groupName, final @Nonnull UUID playerId) {
        val offlinePlayer = getPlayer.apply(playerId);
        if (offlinePlayer != null) {
            return hideGroup(groupName, offlinePlayer);
        } else return false;
    }

    @Override
    public boolean clearGroup(final @Nonnull String groupName, final @Nonnull OfflinePlayer offlinePlayer) {
        if (!Boolean.TRUE.equals(isGroupPresent(groupName))) return false;

        val event = new ClearFakeBlockGroupEvent(offlinePlayer, groupName);
        pluginManager.callEvent(event);
        if (!event.isCancelled()) {
            val playerId = offlinePlayer.getUniqueId();
            val groups = playerGroups.get(playerId);
            if (groups == null) {
                return false;
            } else {
                var isCleared = false;
                if (groups.containsKey(groupName)) {
                    groups.remove(groupName);
                    playerGroupConfig.save(generatePlayerGroups());
                    isCleared = true;

                    if (offlinePlayer instanceof Player player) {
                        val isShownByDefault = groupService.isGroupShownByDefault(groupName);
                        if (isShownByDefault) {
                            groupService.getChunklets(groupName, player.getWorld())
                                    .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet));
                        } else {
                            groupService.getChunklets(groupName, player.getWorld())
                                    .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet, Material.AIR));
                        }
                    }
                }
                return isCleared;
            }
        } else return false;
    }

    @Override
    public boolean clearGroup(final @Nonnull String groupName, final @Nonnull UUID playerId) {
        val offlinePlayer = getPlayer.apply(playerId);
        if (offlinePlayer != null) {
            return clearGroup(groupName, offlinePlayer);
        } else return false;
    }

    @Override
    public @Nullable Map<String, Boolean> clearGroups(final @Nonnull UUID playerId) {
        val offlinePlayer = getPlayer.apply(playerId);
        val groups = playerGroups.remove(playerId);
        groups.forEach((groupName, a) -> {
            if (offlinePlayer instanceof Player player) {
                val isShownByDefault = groupService.isGroupShownByDefault(groupName);
                if (isShownByDefault) {
                    groupService.getChunklets(groupName, player.getWorld())
                            .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet));
                } else {
                    groupService.getChunklets(groupName, player.getWorld())
                            .forEach(chunklet -> protocolLibService.sendChunklet(player, chunklet, Material.AIR));
                }
            }
        });
        playerGroupConfig.save(generatePlayerGroups());
        return groups;
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
