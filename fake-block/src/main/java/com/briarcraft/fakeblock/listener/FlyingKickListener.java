package com.briarcraft.fakeblock.listener;

import lombok.val;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import javax.annotation.Nonnull;

public class FlyingKickListener extends AbstractFilter implements Listener {
    public FlyingKickListener() {
        val logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(final @Nonnull PlayerKickEvent event) {
        if (event.getCause() == PlayerKickEvent.Cause.FLYING_PLAYER ||
                event.getCause() == PlayerKickEvent.Cause.FLYING_VEHICLE) {
            event.leaveMessage(Component.empty());
            event.reason(Component.empty());
            event.setCancelled(true);
        }
    }

    @Override
    public Result filter(final LogEvent event) {
        val source = event.getSource();
        if (source != null) {
            if (source.getClassName().equals("net.minecraft.server.network.PlayerConnection")) {
                if (event.getMessage().getFormattedMessage().contains("was kicked for floating")) {
                    return Result.DENY;
                }
                return Result.NEUTRAL;
            }
        }
        return Result.NEUTRAL;
    }
}
