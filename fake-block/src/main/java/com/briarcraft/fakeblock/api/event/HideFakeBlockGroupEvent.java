package com.briarcraft.fakeblock.api.event;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class HideFakeBlockGroupEvent extends Event implements Cancellable {
    private static final @Nonnull HandlerList handlers = new HandlerList();

    public static @Nonnull HandlerList getHandlerList() {
        return handlers;
    }

    private final @Nonnull OfflinePlayer player;
    private final @Nonnull String groupName;
    private boolean isCancelled;

    public HideFakeBlockGroupEvent(final @Nonnull OfflinePlayer player, final @Nonnull String groupName) {
        this.player = player;
        this.groupName = groupName;
    }

    public @Nonnull OfflinePlayer getPlayer() {
        return player;
    }

    public @Nonnull String getGroupName() {
        return groupName;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @Nonnull HandlerList getHandlers() {
        return handlers;
    }
}
