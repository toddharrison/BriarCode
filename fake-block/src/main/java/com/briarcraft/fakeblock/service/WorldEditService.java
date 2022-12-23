package com.briarcraft.fakeblock.service;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import lombok.val;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class WorldEditService {
    private final @Nonnull SessionManager manager;
    private final @Nonnull Function<Player, BukkitPlayer> adaptPlayer;

    public WorldEditService(final @Nonnull SessionManager manager, final @Nonnull Function<Player, BukkitPlayer> adaptPlayer) {
        this.manager = manager;
        this.adaptPlayer = adaptPlayer;
    }

    public @Nullable Region getPlayerSelection(final @Nonnull Player player) {
        val localSession = manager.get(adaptPlayer.apply(player));
        val world = localSession.getSelectionWorld();
        try {
            return localSession.getSelection(world);
        } catch (final @Nonnull IncompleteRegionException e) {
            return null;
        }
    }
}
