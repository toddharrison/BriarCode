package com.briarcraft.fakeblock.api.service;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This service manages the Groups that are shown and hidden from Players.
 */
public interface PlayerGroupService {
    /**
     * Determine if the specified Group name is shown to the specified Player.
     * @param groupName The name of the Group.
     * @param player The Player.
     * @return True if the Group is shown to the Player, false otherwise.
     */
    boolean isShown(@Nonnull String groupName, @Nonnull OfflinePlayer player);

    /**
     * Determine if the specified Group name is hidden from the specified Player.
     * @param groupName The name of the Group.
     * @param player The Player.
     * @return True if the Group is hidden from the Player, false otherwise.
     */
    boolean isHidden(@Nonnull String groupName, @Nonnull OfflinePlayer player);

    /**
     * Get the Map of configured Groups for the specified Player. True is shown and false is hidden, otherwise use group
     * default visibility.
     * @param player The Player.
     * @return The Map of Group configurations.
     */
    @Nonnull Map<String, Boolean> getConfiguredGroups(@Nonnull OfflinePlayer player);

    /**
     * Get the UUIDs for the Players that have visibility of the specified Group name.
     * @param groupName The Group name.
     * @return The Set of Player unique UUIDs that have visibility to the Group name.
     */
    @Nonnull Set<UUID> getPlayersConfiguredIn(@Nonnull String groupName);

    /**
     * Show the specified Group name to the specified Player UUID. This happens immediately if the Player is online.
     * @param groupName The Group name.
     * @param playerID The Player unique UUID.
     * @return True if the specified Group was shown to the specified Player, false otherwise.
     */
    boolean showGroup(@Nonnull String groupName, @Nonnull UUID playerID);

    /**
     * Show the specified Group name to the specified OfflinePlayer. This happens immediately if the Player is online.
     * @param groupName The Group name.
     * @param player The OfflinePlayer.
     * @return True if the specified Group was shown to the specified OfflinePlayer, false otherwise.
     */
    boolean showGroup(@Nonnull String groupName, @Nonnull OfflinePlayer player);

    /**
     * Hide the specified Group name from the specified Player UUID. This happens immediately if the Player is online.
     * @param groupName The Group name.
     * @param playerId The Player unique UUID.
     * @return True if the specified Group was hidden from the specified Player, false otherwise.
     */
    boolean hideGroup(@Nonnull String groupName, @Nonnull UUID playerId);

    /**
     * Hide the specified Group name from the specified OfflinePlayer. This happens immediately if the Player is online.
     * @param groupName The Group name.
     * @param player The OfflinePlayer.
     * @return True if the specified Group was hidden from the specified Player, false otherwise.
     */
    boolean hideGroup(@Nonnull String groupName, @Nonnull OfflinePlayer player);

    /**
     * Clear the specified Group name from the specified Player UUID configuration. This happens immediately if the
     * Player is online.
     * @param groupName The Group name.
     * @param playerId The Player unique UUID.
     * @return True if the specified Group was cleared from the specified Player, false otherwise.
     */
    boolean clearGroup(@Nonnull String groupName, @Nonnull UUID playerId);

    /**
     * Clear the specified Group name from the specified OfflinePlayer configuration. This happens immediately if the
     * Player is online.
     * @param groupName The Group name.
     * @param player The OfflinePlayer.
     * @return True if the specified Group was cleared from the specified Player, false otherwise.
     */
    boolean clearGroup(@Nonnull String groupName, @Nonnull OfflinePlayer player);

    /**
     * Clear all Groups from the specified OfflinePlayer configuration. This happens immediately if the Player is
     * online.
     * @param playerId The Player unique UUID.
     * @return The Map of Group configuration the Player had, or null if there was none.
     */
    @Nullable Map<String, Boolean> clearGroups(@Nonnull UUID playerId);
}
