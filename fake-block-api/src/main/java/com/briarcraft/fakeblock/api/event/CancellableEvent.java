package com.briarcraft.fakeblock.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public abstract class CancellableEvent extends Event implements Cancellable {

    // TODO: Does every custom event need this separately?
    private static final @Nonnull HandlerList handlers = new HandlerList();
    public static @Nonnull HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public @Nonnull HandlerList getHandlers() {
        return handlers;
    }



    private boolean isCancelled;

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
