package com.briarcraft.fakeblock.api.data;

import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@SerializableAs("Group")
@Data
public class Group implements Comparable<Group>, ConfigurationSerializable {
    private final @Nonnull String name;
    private final @Nonnull World world;
    private final @Nonnull Set<FakeBlock> fakeBlocks;

    @Override
    public @Nonnull Map<String, Object> serialize() {
        val map = new TreeMap<>(String::compareTo);
        map.put("name", name);
        map.put("world", world.getName());
        map.put("fakeBlocks", fakeBlocks.stream().sorted().toList());
        return map;
    }

    public static @Nonnull Group deserialize(final @Nonnull Map<String, Object> args) {
        val name = (String) args.get("name");
        val world = Bukkit.getWorld((String) args.get("world"));
        val fakeBlocks = ((List<FakeBlock>) args.get("fakeBlocks")).stream().collect(Collectors.toSet());
        return new Group(name, world, fakeBlocks);
    }

    @Override
    public int compareTo(final @Nonnull Group group) {
        return name.compareTo(group.getName());
    }
}
