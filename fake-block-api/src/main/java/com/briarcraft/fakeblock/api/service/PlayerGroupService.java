package com.briarcraft.fakeblock.api.service;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

/**
 * This service manages the Groups that are shown and hidden from Players.
 */
public interface PlayerGroupService {
    /**
     * Determine if the specified Group name is visible to the specified Player.
     * @param groupName The name of the Group.
     * @param player The Player.
     * @return True if the Group is visible to the Player, false otherwise.
     */
    boolean isVisible(@Nonnull String groupName, @Nonnull OfflinePlayer player);

    /**
     * Get the Set of Groups visible to the specified Player.
     * @param player The Player.
     * @return The Set of Groups visible.
     */
    @Nonnull Set<String> getVisibleGroups(@Nonnull OfflinePlayer player);

    /**
     * Get the UUIDs for the Players that have visibility of the specified Group name.
     * @param groupName The Group name.
     * @return The Set of Player unique UUIDs that have visibility to the Group name.
     */
    @Nonnull Set<UUID> getPlayersVisibleTo(@Nonnull String groupName);

    /**
     * Show the specified Group name to the specified Player UUID.
     * @param groupName The Group name.
     * @param playerID The Player unique UUID.
     * @return True if the specified Group was shown to the specified Player, false otherwise.
     */
    boolean showGroup(@Nonnull String groupName, @Nonnull UUID playerID);

    /**
     * Show the specified Group name to the specified Player.
     * @param groupName The Group name.
     * @param player The Player.
     * @return True if the specified Group was shown to the specified Player, false otherwise.
     */
    boolean showGroup(@Nonnull String groupName, @Nonnull Player player);

    /**
     * Show the specified Group name to the specified OfflinePlayer.
     * @param groupName The Group name.
     * @param player The OfflinePlayer.
     * @return True if the specified Group was shown to the specified OfflinePlayer, false otherwise.
     */
    boolean showGroup(@Nonnull String groupName, @Nonnull OfflinePlayer player);

    /**
     * Hide the specified Group name from the specified Player UUID.
     * @param groupName The Group name.
     * @param playerId The Player unique UUID.
     * @return True if the specified Group was hidden from the specified Player, false otherwise.
     */
    boolean hideGroup(@Nonnull String groupName, @Nonnull UUID playerId);

    /**
     * Hide the specified Group name from the specified Player.
     * @param groupName The Group name.
     * @param player The Player.
     * @return True if the specified Group was hidden from the specified Player, false otherwise.
     */
    boolean hideGroup(@Nonnull String groupName, @Nonnull Player player);

    /**
     * Hide the specified Group name from the specified OfflinePlayer.
     * @param groupName The Group name.
     * @param player The OfflinePlayer.
     * @return True if the specified Group was hidden from the specified Player, false otherwise.
     */
    boolean hideGroup(@Nonnull String groupName, @Nonnull OfflinePlayer player);
}
