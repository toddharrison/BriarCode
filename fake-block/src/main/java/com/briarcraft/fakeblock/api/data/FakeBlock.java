package com.briarcraft.fakeblock.api.data;

import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@SerializableAs("FakeBlock")
@Data
public class FakeBlock implements Comparable<FakeBlock>, ConfigurationSerializable {
    private final @Nonnull BlockPosition position;
    private final @Nonnull BlockData blockData;

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        val fakeBlock = (FakeBlock) o;
        return position.equals(fakeBlock.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public @Nonnull Map<String, Object> serialize() {
        val map = new TreeMap<>(String::compareTo);
        map.put("position", position.toVector());
        map.put("blockData", blockData.getAsString());
        return map;
    }

    public static @Nonnull FakeBlock deserialize(final @Nonnull Map<String, Object> args) {
        return new FakeBlock(
                new BlockPosition((Vector) args.get("position")),
                Bukkit.getServer().createBlockData((String) args.get("blockData"))
        );
    }

    @Override
    public int compareTo(final @Nonnull FakeBlock fakeBlock) {
        val x = Integer.compare(position.getX(), fakeBlock.position.getX());
        if (x == 0) {
            val z = Integer.compare(position.getZ(), fakeBlock.position.getZ());
            if (z == 0) {
                val y = Integer.compare(position.getY(), fakeBlock.position.getY());
                if (y == 0) {
                    return blockData.getAsString().compareTo(fakeBlock.blockData.getAsString());
                } else return y;
            } else return z;
        } else return x;
    }
}
