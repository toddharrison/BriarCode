//package com.briarcraft.fakeblock
//
//import com.briarcraft.kotlin.util.BlockLocation
//import com.briarcraft.kotlin.util.ChunkLocation
//import com.briarcraft.kotlin.util.ChunkletLocation
//import com.comphenix.protocol.PacketType
//import com.comphenix.protocol.ProtocolLibrary
//import com.comphenix.protocol.ProtocolManager
//import com.comphenix.protocol.events.ListenerPriority
//import com.comphenix.protocol.events.PacketAdapter
//import com.comphenix.protocol.events.PacketContainer
//import com.comphenix.protocol.events.PacketEvent
//import com.comphenix.protocol.reflect.StructureModifier
//import com.comphenix.protocol.wrappers.BlockPosition
//import com.comphenix.protocol.wrappers.WrappedBlockData
//import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
//import com.github.shynixn.mccoroutine.bukkit.launch
//import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
//import kotlinx.coroutines.delay
//import org.bukkit.Bukkit
//import org.bukkit.Chunk
//import org.bukkit.Location
//import org.bukkit.Material
//import org.bukkit.World
//import org.bukkit.block.BlockFace
//import org.bukkit.block.data.Bisected
//import org.bukkit.block.data.BlockData
//import org.bukkit.block.data.type.Stairs
//import org.bukkit.entity.Player
//import org.bukkit.event.EventHandler
//import org.bukkit.event.EventPriority
//import org.bukkit.event.HandlerList
//import org.bukkit.event.Listener
//import org.bukkit.event.player.PlayerJoinEvent
//import org.bukkit.event.player.PlayerKickEvent
//import org.bukkit.event.player.PlayerQuitEvent
//import org.bukkit.plugin.Plugin
//import java.util.*
//import kotlin.math.floor
//import kotlin.math.min
//import kotlin.time.Duration.Companion.seconds
//
//@Suppress("unused")
//class FakeBlockPlugin2: SuspendingJavaPlugin() {
//    override suspend fun onLoadAsync() {
//        saveDefaultConfig()
//    }
//
//    override suspend fun onEnableAsync() {
//        val packetAdapter = FakeBlockPacketAdapter(this)
//        val protocolManager = ProtocolLibrary.getProtocolManager()
//        protocolManager.addPacketListener(packetAdapter)
//
//        server.pluginManager.registerSuspendingEvents(object: Listener {
//            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//            suspend fun on(event: PlayerJoinEvent) {
//                val player = event.player
//                val world = player.world
//
//                delay(5.seconds)
//
//                val blockData = Bukkit.createBlockData(Material.STONE_STAIRS) as Stairs
//                blockData.half = Bisected.Half.BOTTOM
//                blockData.facing = BlockFace.SOUTH
//
//                sendBlockChanges(protocolManager, player, mapOf(
//                    Location(world, 0.0, 158.0, 0.0) to blockData,
//                    Location(world, 0.0, 159.0, 0.0) to blockData,
//                    Location(world, 0.0, 160.0, 0.0) to blockData,
//                    Location(world, 0.0, 161.0, 0.0) to blockData,
//                    Location(world, 0.0, 162.0, 0.0) to blockData,
//                ))
//            }
//
//            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//            suspend fun on(event: PlayerKickEvent) {
//                when (event.cause) {
//                    PlayerKickEvent.Cause.FLYING_PLAYER -> {
//                        println("Player flying")
//                    }
//                    else -> {}
//                }
//            }
//        }, this)
//    }
//
//    override suspend fun onDisableAsync() {
//        HandlerList.unregisterAll(this)
//    }
//}
//
//
//
//
//
//data class FakeBlock(
//    val location: BlockLocation,
//    val blockData: BlockData
//)
//
//data class FakeBlockChunklet(
//    val world: World,
//    val location: ChunkletLocation,
//    val fakeBlocks: List<FakeBlock>
//)
//
//class FakeBlockGroupManager() {
//    fun create(group: String, blocks: List<FakeBlock>) {}
//}
//
//
//
//
//
//class FakeBlockGroupManager2(
//    private val groups: MutableMap<String, MutableList<FakeBlockChunklet>>
//) {
//    private val chunks: MutableMap<ChunkLocation, MutableList<FakeBlockChunklet>> = groups.values
//        .flatten()
//        .groupBy { it.location.toChunkLocation() }
//        .mapValues { it.value.toMutableList() }
//        .toMutableMap()
//
//    val names: Set<String> get() = groups.keys
//
//    fun create(group: String, data: List<FakeBlockChunklet>): Boolean {
//        return if (groups.containsKey(group)) false
//        else {
//            groups[group] = data.toMutableList()
//            data.forEach { chunks.getOrPut(it.location.toChunkLocation()) { mutableListOf() }.add(it) }
//            true
//        }
//    }
//
//    fun getChunklets(group: String): List<FakeBlockChunklet>? = groups[group]
//
//    fun getChunklets(location: ChunkLocation): List<FakeBlockChunklet>? = chunks[location]
//
//    fun add(group: String, data: List<FakeBlockChunklet>): Boolean {
//        return groups[group]?.let { g ->
//            val groupLocations = g.map { it.location }
//            val dataLocations = data.map { it.location }
//            if (dataLocations.none { groupLocations.contains(it) }) {
//                data.forEach { chunks.getOrPut(it.location.toChunkLocation()) { mutableListOf() }.add(it) }
//                g.addAll(data)
//            } else false
//        } ?: false
//    }
//
//    fun remove(group: String, locations: List<ChunkletLocation>): Boolean {
//        return groups[group]?.let { g ->
//            val groupLocations = g.map { it.location }
//            if (locations.all { groupLocations.contains(it) }) {
//                g.removeIf { locations.contains(it.location) }
//            } else false
//        } ?: false
//    }
//
//    fun delete(group: String) = groups.remove(group) != null
//}
//
//
//
//
//
//
//
//
//
//
//class FakeBlockPacketAdapter(
//    plugin: Plugin,
//    private val fakeBlockManager: FakeBlockManager,
//    private val protocolManager: ProtocolManager
//): PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.BLOCK_CHANGE, PacketType.Play.Server.MAP_CHUNK) {
//    override fun onPacketSending(event: PacketEvent) {
//        if (event.isCancelled) return
//
//        val packet = event.packet
//        val player = event.player
//        val world = player.world
//        when (event.packetType) {
//            PacketType.Play.Server.BLOCK_CHANGE -> {
//                val position = packet.blockPositionModifier.read(0)
//                if (position != null) {
//                    val fakeBlock = fakeBlockManager.getFakeBlock(player, world, position)
//                    if (fakeBlock != null) {
//                        packet.blockData.write(0, fakeBlock)
//                    }
//                }
//            }
//            PacketType.Play.Server.MAP_CHUNK -> {
//                val ints = packet.integers
//                val chunkX = ints.read(0)
//                val chunkZ = ints.read(1)
//                val chunk = world.getChunkAt(chunkX, chunkZ)
//
//                plugin.launch {
//                    while (!chunk.isLoaded) {
//                        delay(1.seconds)
//                    }
//                    val fakeBlocks = fakeBlockManager.getFakeBlocks(player, world, ChunkLocation(chunkX, chunkZ))
//                    if (fakeBlocks) {
//                        sendBlockChanges(protocolManager, player, fakeBlocks)
//                    }
//                }
//            }
//        }
//    }
//}
//
//class FakeBlockManager(private val protocolManager: ProtocolManager) {
//    fun getFakeBlock(player: Player, world: World, position: BlockPosition): WrappedBlockData? {
//        // TODO
//        return if (position.x == 0 && position.y == 100 && position.z == 0) {
//            val blockData = (Bukkit.createBlockData(Material.STONE_STAIRS) as Stairs)
//                .also {
//                    it.half = Bisected.Half.BOTTOM
//                    it.facing = BlockFace.SOUTH
//                }
//            WrappedBlockData.createData(blockData)
//        } else null
//    }
//
//    fun sendFakeBlocks(player: Player, world: World, chunkLoc: ChunkLocation) {
//        val blockLoc = BlockLocation(100, 0, 100)
//        val chunkletLoc = toChunkletBlockPosition(blockLoc)
//        if (blockLoc.toChunkLocation() == chunkLoc) {
//            val packet = protocolManager.createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE)
//            packet.sectionPositions.writeSafely(0, chunkletLoc)
//            packet.blockDataArrays.writeSafely(0, blocks
//                .map { (_, data) -> WrappedBlockData.createData(data) }
//                .toTypedArray())
//            packet.shortArrays.writeSafely(0, blocks
//                .map { (location, _) -> toChunkletRelativeLocationAsShort(location) }
//                .toShortArray())
//            protocolManager.sendServerPacket(player, packet)
//        }
//    }
//}
//
//
//
//
//
//
//
//class BlockManager(private val protocolManager: ProtocolManager) {
//    private val blocks = mutableMapOf<UUID, MutableMap<Location, VisualBlock>>()
//
//    fun sendBlock(player: Player, blockType: Material, dataId: Int, location: Location) {
//        val blockChangePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE)
//        val typeBlock = WrappedBlockData.createData(blockType, dataId)
//        val blockPosition = BlockPosition(location.blockX, location.blockY, location.blockZ)
//        blockChangePacket.blockPositionModifier.write(0, blockPosition)
//        blockChangePacket.blockData.write(0, typeBlock)
//        addVisualBlock(player.uniqueId, VisualBlock(player.uniqueId, blockType, dataId = dataId), location)
//        protocolManager.sendServerPacket(player, blockChangePacket)
//    }
//
//    fun sendBlock(player: Player, blockData: BlockData, location: Location) {
//        val blockChangePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE)
//        val typeBlock = WrappedBlockData.createData(blockData)
//        val blockPosition = BlockPosition(location.blockX, location.blockY, location.blockZ)
//        blockChangePacket.blockPositionModifier.write(0, blockPosition)
//        blockChangePacket.blockData.write(0, typeBlock)
//        addVisualBlock(player.uniqueId, VisualBlock(player.uniqueId, blockData.material, blockData = blockData), location)
//        protocolManager.sendServerPacket(player, blockChangePacket)
//    }
//
//    fun sendBlockSpigot(player: Player, blockData: BlockData, location: Location) {
//        addVisualBlock(player.uniqueId, VisualBlock(player.uniqueId, blockData.material, blockData = blockData), location)
//        player.sendBlockChange(location, blockData)
//    }
//
//    fun sendBlock(player: Player, blockType: Material, location: Location) {
//        val blockChangePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE)
//        val typeBlock = WrappedBlockData.createData(blockType)
//        val blockPosition = BlockPosition(location.blockX, location.blockY, location.blockZ)
//        blockChangePacket.blockPositionModifier.write(0, blockPosition)
//        blockChangePacket.blockData.write(0, typeBlock)
//        addVisualBlock(player.uniqueId, VisualBlock(player.uniqueId, blockType), location)
//        protocolManager.sendServerPacket(player, blockChangePacket)
//    }
//
//    private fun addVisualBlock(player: UUID, block: VisualBlock, location: Location) {
//        val blocksArray: MutableMap<Location, VisualBlock>?
//        if (!blocks.containsKey(player)) {
//            blocksArray = HashMap()
//        } else {
//            blocksArray = blocks[player]
//        }
//        blocksArray!![location] = block
//        blocks[player] = blocksArray
//    }
//
//    fun clearBlocks(player: UUID?) {
//        blocks.remove(player)
//    }
//
//    fun clearAllVisualBlocks(player: UUID) {
//        if (!blocks.containsKey(player)) return
//        val visualBlocks = blocks[player]
//        val playerObj = Bukkit.getPlayer(player)
//        for ((location) in visualBlocks!!) {
//            if (playerObj != null) {
//                val originalBlock = location.world.getBlockAt(location)
//                sendBlock(playerObj, originalBlock.type, originalBlock.getData().toInt(), location)
//            }
//        }
//        blocks.remove(player)
//    }
//
//    fun clearVisualBlockFill(player: UUID?, min: Location, max: Location) {
//        if (!blocks.containsKey(player)) return
//        val visualBlocks = blocks.get(player)
//        val toDeleteLocations: MutableList<Location> = ArrayList()
//        val playerObj = Bukkit.getPlayer(player!!) ?: return
//        for ((blockPosition) in visualBlocks!!) {
//            val firstX = min.blockX
//            val firstY = min.blockY
//            val firstZ = min.blockZ
//            val secondX = max.blockX
//            val secondY = max.blockY
//            val secondZ = max.blockZ
//            val x = blockPosition.blockX
//            val y = blockPosition.blockY
//            val z = blockPosition.blockZ
//            if (x >= floor(min(max.blockX, min.blockX).toDouble()).toInt() && z >= floor(min(max.blockZ, min.blockZ).toDouble()).toInt() && y >= floor(min(max.blockY, min.blockY).toDouble()).toInt()) {
//                val originalBlock = blockPosition.world.getBlockAt(blockPosition)
//                toDeleteLocations.add(blockPosition)
//                sendBlock(playerObj, originalBlock.type, originalBlock.getData().toInt(), blockPosition)
//            }
//        }
//        for (location in toDeleteLocations) {
//            if (!visualBlocks.containsKey(location)) continue
//            visualBlocks.remove(location)
//        }
//    }
//
//    fun clearVisualBlock(player: UUID?, location: Location) {
//        if (!blocks.containsKey(player)) return
//        val visualBlocks = blocks.get(player)
//        val toDeleteLocations: MutableList<Location> = ArrayList()
//        val playerObj = Bukkit.getPlayer(player!!) ?: return
//        for ((blockPosition) in visualBlocks!!) {
//            if (location.blockX == blockPosition.blockX && location.blockY == blockPosition.blockY && location.blockZ == blockPosition.blockZ && location.world === blockPosition.world) {
//                val originalBlock = location.world.getBlockAt(location)
//                toDeleteLocations.add(blockPosition)
//                sendBlock(playerObj, originalBlock.type, originalBlock.getData().toInt(), location)
//            }
//        }
//        for (locationToDelete in toDeleteLocations) {
//            if (!visualBlocks.containsKey(locationToDelete)) continue
//            visualBlocks.remove(locationToDelete)
//        }
//    }
//
//    fun getVisualBlock(player: Player, blockPosition: Location): VisualBlock? {
//        if (!blocks.containsKey(player.uniqueId)) { return null }
//
//        val visualBlocks = blocks[player.uniqueId]
//        for ((location, block) in visualBlocks!!) {
//            if (location.blockX == blockPosition.blockX && location.blockY == blockPosition.blockY && location.blockZ == blockPosition.blockZ && location.world === blockPosition.world) return block
//        }
//        return null
//    }
//
//    fun hasVisualBlocks(player: Player): Boolean {
//        return blocks.containsKey(player.uniqueId)
//    }
//}
//
//class CoreListener(private val manager: BlockManager) : Listener {
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
//    fun onDisconnect(event: PlayerQuitEvent) {
//        manager.clearBlocks(event.player.uniqueId)
//    }
//}
//
//class MyPacketAdapter(plugin: Plugin, private val manager: BlockManager): PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.BLOCK_CHANGE) {
//    override fun onPacketSending(event: PacketEvent) {
//        if (event.isCancelled) return
//
//        if (event.packetType == PacketType.Play.Server.BLOCK_CHANGE) {
//            val wrapper = WrapperPlayServerBlockChange(event.packet)
//            val blockLocation = wrapper.getLocation()
//            if (blockLocation != null && hasBeenFaked(event.player, blockLocation)) {
//                val block = getVisualBlock(event.player, blockLocation)
//                if (block != null) {
//                    val dataId = block.dataId
//                    val newBlockData = if (dataId != null) {
//                        WrappedBlockData.createData(block.type, dataId)
//                    } else {
//                        WrappedBlockData.createData(block.blockData)
//                    }
//                    wrapper.setBlockData(newBlockData)
//                }
//            }
//        }
//    }
//
//    // A helper method to determine if a block at a location has been faked
//    private fun hasBeenFaked(player: Player, location: BlockPosition): Boolean {
//        return getVisualBlock(player, location) != null
//    }
//
//    private fun getVisualBlock(player: Player, blockPosition: BlockPosition): VisualBlock? {
//        val location = Location(player.world, blockPosition.x.toDouble(), blockPosition.y.toDouble(), blockPosition.z.toDouble())
//        return manager.getVisualBlock(player, location)
//    }
//
//    private fun toLocation(world: World, blockPosition: BlockPosition): Location {
//        return Location(world, blockPosition.x.toDouble(), blockPosition.y.toDouble(), blockPosition.z.toDouble())
//    }
//}
//
//class WrapperPlayServerBlockChange(private val handle: PacketContainer) {
//    init {
//        val type = PacketType.Play.Server.BLOCK_CHANGE
////        handle = PacketContainer(type)
//        handle.modifier.writeDefaults()
//    }
//
//    fun sendPacket(player: Player?) {
//        ProtocolLibrary.getProtocolManager().sendServerPacket(player, handle)
//    }
//
//    fun broadcastPacket() {
//        ProtocolLibrary.getProtocolManager().broadcastServerPacket(handle)
//    }
//
//    fun receivePacket(player: Player?) {
//        ProtocolLibrary.getProtocolManager().receiveClientPacket(player, handle)
//    }
//
//    fun getLocation(): BlockPosition? {
//        return handle.blockPositionModifier.read(0)
//    }
//
//    fun setLocation(value: BlockPosition?) {
//        handle.blockPositionModifier.write(0, value)
//    }
//
//    fun getBukkitLocation(world: World): Location? {
//        return getLocation()?.toVector()?.toLocation(world)
//    }
//
//    fun getBlockData(): WrappedBlockData? {
//        return handle.blockData.read(0)
//    }
//
//    fun setBlockData(value: WrappedBlockData?) {
//        handle.blockData.write(0, value)
//    }
//}
//
//data class VisualBlock(
//    val player: UUID,
//    val type: Material,
//    val blockData: BlockData = type.createBlockData(),
//    val dataId: Int? = null
//)
