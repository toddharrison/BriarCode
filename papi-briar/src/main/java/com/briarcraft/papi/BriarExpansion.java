package com.briarcraft.papi;

import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.LongSupplier;

public class BriarExpansion extends PlaceholderExpansion {
    public static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    public static final @Nonnull String PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED = "player_midnights_since_last_played";

    private @Nonnull LongSupplier currentTimeMillis = System::currentTimeMillis;

    public void setTimeResolver(final @Nonnull LongSupplier currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    @Override
    public @Nonnull String getAuthor() {
        return "toddharrison";
    }

    @Override
    public @Nonnull String getIdentifier() {
        return "briar";
    }

    @Override
    public @Nonnull String getVersion() {
        return "1.0.0";
    }

    @SuppressWarnings({"deprecation", "SwitchStatementWithTooFewBranches"})
    @Override
    public @Nullable String onRequest(final @Nullable OfflinePlayer player, final @Nonnull String params) {
        switch (params.toLowerCase()) {
            case PLAYER_MIDNIGHTS_SINCE_LAST_PLAYED -> {
                if (player != null) {
                    val lastPlayed = player.getLastPlayed();
                    val now = currentTimeMillis.getAsLong();
                    val millisSinceLastPlayed = now - lastPlayed;
                    val millisSinceMidnight = now % MILLIS_PER_DAY;

                    var midnightsSinceLastPlayed = 0;
                    if (millisSinceMidnight < millisSinceLastPlayed) midnightsSinceLastPlayed += 1;
                    midnightsSinceLastPlayed += (millisSinceLastPlayed - millisSinceMidnight) / MILLIS_PER_DAY;

                    return Long.toString(midnightsSinceLastPlayed);
                } else return null;
            }
            default -> { return null; }
        }
    }
}
