package com.briarcraft.fakeblock.data;

import lombok.Data;
import lombok.val;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@SerializableAs("PlayerGroup")
@Data
public class PlayerGroup implements Comparable<PlayerGroup>, ConfigurationSerializable {
    private static final @Nonnull String ARG_PLAYER_ID = "playerId";
    private static final @Nonnull String ARG_GROUPS = "groups";

    private final @Nonnull UUID playerId;
    private final @Nonnull Map<String, Boolean> groupNames;

    @Override
    public @Nonnull Map<String, Object> serialize() {
        val map = new TreeMap<>(String::compareTo);
        map.put(ARG_PLAYER_ID, playerId.toString());
        val groups = new TreeMap<>(groupNames);
        map.put(ARG_GROUPS, groups);
        return map;
    }

    @SuppressWarnings("unused")
    public static @Nonnull PlayerGroup deserialize(final @Nonnull Map<String, Object> args) {
        val playerId = UUID.fromString((String) args.get(ARG_PLAYER_ID));
        val groupNames = ((Map<?, ?>) args.get(ARG_GROUPS)).entrySet().stream()
                .filter(entry -> entry.getKey() instanceof String)
                .filter(entry -> entry.getValue() instanceof Boolean)
                .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (Boolean) entry.getValue()));
        return new PlayerGroup(playerId, groupNames);
    }

    @Override
    public int compareTo(final @Nonnull PlayerGroup playerGroup) {
        return playerId.compareTo(playerGroup.getPlayerId());
    }
}
