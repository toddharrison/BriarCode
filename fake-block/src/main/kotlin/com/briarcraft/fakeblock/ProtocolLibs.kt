//package com.briarcraft.fakeblock
//
//import com.briarcraft.kotlin.util.BlockLocation
//import com.comphenix.protocol.PacketType
//import com.comphenix.protocol.ProtocolManager
//import com.comphenix.protocol.events.PacketContainer
//import com.comphenix.protocol.wrappers.BlockPosition
//import com.comphenix.protocol.wrappers.WrappedBlockData
import org.bukkit.Location
//import org.bukkit.block.data.BlockData
//import org.bukkit.entity.Player
//
//fun sendBlockChange(protocolManager: ProtocolManager, player: Player, location: Location, blockData: BlockData) {
//    val packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE)
//    val typeBlock = WrappedBlockData.createData(blockData)
//    val blockPosition = BlockPosition(location.toVector())
//    packet.blockPositionModifier.writeSafely(0, blockPosition)
//    packet.blockData.writeSafely(0, typeBlock)
//    protocolManager.sendServerPacket(player, packet)
//}
//
//fun sendBlockChanges(protocolManager: ProtocolManager, player: Player, changes: Map<Location, BlockData>) {
//    changes.entries.groupBy { (location, _) ->
//        toChunkletBlockPosition(location)
//    }.forEach { (chunkletLoc, blocks) ->
//        val packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE)
//        packet.sectionPositions.writeSafely(0, chunkletLoc)
//        packet.blockDataArrays.writeSafely(0, blocks
//            .map { (_, data) -> WrappedBlockData.createData(data) }
//            .toTypedArray())
//        packet.shortArrays.writeSafely(0, blocks
//            .map { (location, _) -> toChunkletRelativeLocationAsShort(location) }
//            .toShortArray())
//        protocolManager.sendServerPacket(player, packet)
//    }
//}
//
//fun updatePacket(packet: PacketContainer, location: Location, blockData: () -> BlockData) {
//    when (packet.type) {
//        PacketType.Play.Server.BLOCK_CHANGE -> {
//            val position = packet.blockPositionModifier.read(0)
//            if (position != null) {
//                if (position.x == location.blockX && position.y == location.blockY && position.z == location.blockZ) {
//                    val fakeBlockData = WrappedBlockData.createData(blockData())
//                    packet.blockData.write(0, fakeBlockData)
//                }
//            }
//        }
//        else -> println(packet.type)
//    }
//}
//
//
//
//fun toChunkletBlockPosition(location: Location) =
//    toChunkletBlockPosition(location.blockX, location.blockY, location.blockZ)
//
//fun toChunkletBlockPosition(location: BlockLocation) =
//    toChunkletBlockPosition(location.x, location.y, location.z)
//
//fun toChunkletBlockPosition(x: Int, y: Int, z: Int) =
//    BlockPosition(x shr 4, y shr 4, z shr 4)
//
//
//fun toChunkletRelativeLocationAsShort(location: Location) =
//    (location.blockX and 0xF shl 8 or (location.blockZ and 0xF shl 4) or (location.blockY and 0xF shl 0)).toShort()
