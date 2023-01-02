package com.briarcraft.fakeblock.service;

import com.briarcraft.fakeblock.api.data.Chunklet;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class ProtocolLibService {
    private final @Nonnull ProtocolManager protocolManager;

    public @Nonnull WrappedBlockData wrapBlockData(final @Nonnull BlockData blockData) {
        return WrappedBlockData.createData(blockData);
    }

    public @Nonnull PacketContainer createPacket(final @Nonnull Location location, final @Nonnull BlockData blockData) {
        val packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        val blockPosition = new BlockPosition(location.toVector());
        val typeBlock = WrappedBlockData.createData(blockData);
        packet.getBlockPositionModifier().writeSafely(0, blockPosition);
        packet.getBlockData().writeSafely(0, typeBlock);
        return packet;
    }

    public void sendBlock(final @Nonnull Player player, final @Nonnull Location location, final @Nonnull BlockData blockData) {
        protocolManager.sendServerPacket(player, createPacket(location, blockData));
    }

    public @Nonnull PacketContainer createPacket(final @Nonnull Chunklet chunklet) {
        val packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        packet.getSectionPositions().writeSafely(0, chunklet.getPosition());
        packet.getBlockDataArrays().writeSafely(0, chunklet.getBlockData());
        packet.getShortArrays().writeSafely(0, chunklet.getRelativeLocations());
        return packet;
    }

    public @Nonnull PacketContainer createPacket(final @Nonnull Chunklet chunklet, final @Nonnull Material newType) {
        val newBlockData = new WrappedBlockData[chunklet.getBlockData().length];
        for (var i = 0; i < newBlockData.length; i++) {
            newBlockData[i] = WrappedBlockData.createData(newType);
        }

        val packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        packet.getSectionPositions().writeSafely(0, chunklet.getPosition());
        packet.getBlockDataArrays().writeSafely(0, newBlockData);
        packet.getShortArrays().writeSafely(0, chunklet.getRelativeLocations());
        return packet;
    }

    public void sendChunklet(final @Nonnull Player player, final @Nonnull Chunklet chunklet) {
        protocolManager.sendServerPacket(player, createPacket(chunklet));
    }

    public void sendChunklet(final @Nonnull Player player, final @Nonnull Chunklet chunklet, final @Nonnull Material newType) {
        protocolManager.sendServerPacket(player, createPacket(chunklet, newType));
    }
}
