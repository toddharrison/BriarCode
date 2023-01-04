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

/**
 * This data class represents a fake-block Group, consisting of a name, World, and Set of FakeBlocks it comprises. It is
 * Serializable and designed to use Bukkit built-in class persistence. Groups are considered the same if they have the
 * same name.
 */
@SerializableAs("Group")
@Data
public class Group implements Comparable<Group>, ConfigurationSerializable {
    private static final @Nonnull String ARG_NAME = "name";
    private static final @Nonnull String ARG_WORLD = "world";
    private static final @Nonnull String ARG_BLOCKS = "fakeBlocks";
    private static final @Nonnull String ARG_SHOW_DEFAULT = "isShowDefault";

    private final @Nonnull String name;
    private final @Nonnull World world;
    private final @Nonnull Set<FakeBlock> fakeBlocks;
    private boolean isShownByDefault;

    @Override
    public @Nonnull Map<String, Object> serialize() {
        val map = new TreeMap<>(String::compareTo);
        map.put(ARG_NAME, name);
        map.put(ARG_WORLD, world.getName());
        map.put(ARG_BLOCKS, fakeBlocks.stream().sorted().toList());
        map.put(ARG_SHOW_DEFAULT, isShownByDefault);
        return map;
    }

    @SuppressWarnings("unused")
    public static @Nonnull Group deserialize(final @Nonnull Map<String, Object> args) {
        val name = (String) args.get(ARG_NAME);
        val world = Bukkit.getWorld((String) args.get(ARG_WORLD));
        val fakeBlocks = ((List<FakeBlock>) args.get(ARG_BLOCKS)).stream().collect(Collectors.toSet());
        val isShowDefault = (Boolean) args.get(ARG_SHOW_DEFAULT);

        val group = new Group(name, world, fakeBlocks);
        group.setShownByDefault(isShowDefault);
        return group;
    }

    @Override
    public int compareTo(final @Nonnull Group group) {
        return name.compareTo(group.getName());
    }
}
