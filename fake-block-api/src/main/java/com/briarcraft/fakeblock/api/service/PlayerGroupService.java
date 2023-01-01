package com.briarcraft.fakeblock.api.service;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

public interface PlayerGroupService {
    boolean isVisible(@Nonnull String groupName, @Nonnull OfflinePlayer player);
    @Nonnull Set<String> getVisibleGroups(@Nonnull OfflinePlayer player);
    @Nonnull Set<UUID> getPlayersVisibleTo(@Nonnull String groupName);
    boolean showGroup(@Nonnull String groupName, @Nonnull UUID playerID);
    boolean showGroup(@Nonnull String groupName, @Nonnull Player player);
    boolean showGroup(@Nonnull String groupName, @Nonnull OfflinePlayer player);
    boolean hideGroup(@Nonnull String groupName, @Nonnull UUID playerId);
    boolean hideGroup(@Nonnull String groupName, @Nonnull Player player);
    boolean hideGroup(@Nonnull String groupName, @Nonnull OfflinePlayer player);
}
