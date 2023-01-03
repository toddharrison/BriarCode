package com.briarcraft.fakeblock.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;

/**
 * This CancellableEvent is called when an existing Group is hidden from a specific Player.
 */
@RequiredArgsConstructor
@Getter
public class HideFakeBlockGroupEvent extends CancellableEvent {
    private final @Nonnull OfflinePlayer player;
    private final @Nonnull String groupName;
}
