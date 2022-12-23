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
    private final @Nonnull UUID playerId;
    private final @Nonnull Set<String> groupNames;

    @Override
    public @Nonnull Map<String, Object> serialize() {
        val map = new TreeMap<>(String::compareTo);
        map.put("playerId", playerId.toString());
        map.put("groups", groupNames.stream().sorted().toList());
        return map;
    }

    public static @Nonnull PlayerGroup deserialize(final @Nonnull Map<String, Object> args) {
        val playerId = UUID.fromString((String) args.get("playerId"));
        val groupNames = ((List<String>) args.get("groups")).stream().collect(Collectors.toSet());
        return new PlayerGroup(playerId, groupNames);
    }

    @Override
    public int compareTo(final @Nonnull PlayerGroup playerGroup) {
        return playerId.compareTo(playerGroup.getPlayerId());
    }
}
