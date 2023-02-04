package com.briarcraft.regionscan

import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.block.BlockState
import org.bukkit.block.Chest
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import java.io.FileWriter
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class WorldPopulator {
    private var tileEntityWriter: FileWriter? = null
    private var entityWriter: FileWriter? = null

    fun populateWorld(plugin: RegionScanPlugin) {
        val tileEntityFilter = { tileEntity: BlockState ->
            when (tileEntity.type) {
                Material.CHEST,
                Material.TRAPPED_CHEST,
                Material.SPAWNER,
                Material.BEE_NEST,
                Material.BELL,
                Material.OAK_WALL_SIGN,
                Material.SPRUCE_WALL_SIGN,
                Material.MAGENTA_WALL_BANNER,
                Material.GRAY_WALL_BANNER,
                Material.LIGHT_GRAY_WALL_BANNER,
                Material.WHITE_WALL_BANNER,
                Material.BROWN_WALL_BANNER,
                Material.BLACK_WALL_BANNER,
                -> true
                Material.BARREL,
                Material.CAMPFIRE,
                Material.BLUE_BED,
                Material.PURPLE_BED,
                Material.YELLOW_BED,
                Material.WHITE_BED,
                Material.RED_BED,
                Material.ORANGE_BED,
                Material.LIME_BED,
                Material.GREEN_BED,
                Material.CYAN_BED,
                Material.FURNACE,
                Material.BLAST_FURNACE,
                Material.BREWING_STAND,
                Material.SMOKER,
                Material.LECTERN,
                -> false
                else -> { println(tileEntity.type); false }
            }
        }
        val entityFilter = { entity: Entity ->
            when (entity.type) {
                EntityType.ARMOR_STAND,
                EntityType.MINECART_CHEST,
                EntityType.VILLAGER,
                EntityType.IRON_GOLEM,
                EntityType.DROWNED,
                EntityType.ELDER_GUARDIAN,
                EntityType.WITCH,
                EntityType.EVOKER,
                EntityType.VINDICATOR,
                EntityType.ILLUSIONER,
                EntityType.ITEM_FRAME,
                EntityType.ZOMBIE_VILLAGER,
                EntityType.SHULKER,
                EntityType.ENDER_CRYSTAL,
                EntityType.GLOW_ITEM_FRAME,
                EntityType.PAINTING,
                EntityType.PIGLIN_BRUTE,
                EntityType.PILLAGER,
                EntityType.RAVAGER,
                -> true
                EntityType.PIG,
                EntityType.COW,
                EntityType.MUSHROOM_COW,
                EntityType.CHICKEN,
                EntityType.WOLF,
                EntityType.SHEEP,
                EntityType.FOX,
                EntityType.RABBIT,
                EntityType.BEE,
                EntityType.HORSE,
                EntityType.DONKEY,
                EntityType.LLAMA,
                EntityType.CAT,
                EntityType.TURTLE,
                EntityType.POLAR_BEAR,
                EntityType.PANDA,
                EntityType.PARROT,
                EntityType.GOAT,
                EntityType.FALLING_BLOCK,
                EntityType.DROPPED_ITEM,
                -> false
                else -> { println(entity.type); false }
            }
        }



        val world = plugin.server.getWorld("briar_briarcraft_nether")!!
        tileEntityWriter = FileWriter("${world.name}_tileEntities.txt", true)
        entityWriter = FileWriter("${world.name}_entities.txt", true)
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, { _ ->
            try {
                val scanner = ChunkScanner(plugin)
                val spiral = getSpiralSequence().iterator()
                val chunks = 0..65_536 // Overworld: 589_824, The End, Under, Nether: 65_536
                var percentComplete = 0

                plugin.logger.info("Loading chunks...")
                val startTime = Instant.now()
                var lastTime = startTime
                chunks.chunked(8).forEach { chunkSet ->
//                    logger.info("Processing chunk ${chunkSet.first()} to ${chunkSet.last()}")
                    val futures = chunkSet.map {
                        val chunkLoc = spiral.next()

//                        367,-364: Overworld
//                        125,60: The End

//                        if (it <= 0) return@map CompletableFuture.completedFuture(true)
                        val future = CompletableFuture<Boolean>()

                        world.getChunkAtAsync(chunkLoc.x, chunkLoc.z)
                            .orTimeout(10, TimeUnit.SECONDS)
                            .whenComplete { chunk: Chunk?, error: Throwable? ->
                                try {
                                    if (chunk != null) {
//                                        logger.info("Started chunk $chunk")
                                        scanner.scan(chunk, tileEntityFilter, entityFilter) { tileEntities, entities ->
                                            require(plugin.server.isPrimaryThread)
                                            tileEntities.forEach {
                                                when (it.type) {
                                                    Material.SPAWNER -> {
                                                        val spawnerType = (it.block.state as CreatureSpawner).spawnedType
                                                        tileEntityWriter?.write("${it.type.name}\t${it.x}\t${it.y}\t${it.z}\t${it.blockData.asString}\t$spawnerType\n")
                                                    }
                                                    Material.CHEST -> {
                                                        val chestType = (it.block.state as Chest).lootTable?.key
                                                        tileEntityWriter?.write("${it.type.name}\t${it.x}\t${it.y}\t${it.z}\t${it.blockData.asString}\t$chestType\n")
                                                    }
                                                    else -> {
                                                        tileEntityWriter?.write("${it.type.name}\t${it.x}\t${it.y}\t${it.z}\t${it.blockData.asString}\n")
                                                    }
                                                }
                                            }
                                            entities.forEach {
                                                entityWriter?.write("${it.type.name}\t${it.location.x}\t${it.location.y}\t${it.location.z}\t${it.facing.direction}\n")
                                            }
                                            tileEntityWriter?.flush()
                                            entityWriter?.flush()
                                        }.thenAccept {
                                            future.complete(true)
                                            chunk.unload()
//                                            logger.info("Completed chunk $chunk")
                                        }
                                    } else if (error != null) {
                                        plugin.logger.log(Level.WARNING, "Loading chunk $chunkLoc failed", error)
                                        future.complete(false)
                                    }
                                } catch (e: Throwable) {
                                    plugin.logger.log(Level.WARNING, "ERROR", e)
                                }
                            }

                        future
                    }

                    require(!plugin.server.isPrimaryThread)
                    CompletableFuture.allOf(*futures.toTypedArray()).get()
//                    logger.info("Completed chunk ${chunkSet.first()} to ${chunkSet.last()}")
                    val completedChunk = chunkSet.last()

                    val nowTime = Instant.now()
                    if (Duration.between(lastTime, nowTime).toMinutes() >= 1) {
                        lastTime = nowTime
                        plugin.logger.info("Completed processing chunk $completedChunk of ${chunks.last} at ${chunks.last / Duration.between(startTime, nowTime).toSeconds()} chunks/second")
                    }

                    val newPercentComplete = ((completedChunk / chunks.last.toDouble()) * 100.0).toInt()
                    if (newPercentComplete != percentComplete) {
                        percentComplete = newPercentComplete
                        plugin.logger.info("Scan progress: $percentComplete%")
                    }
                }

                tileEntityWriter?.close()
                entityWriter?.close()
                tileEntityWriter = null
                entityWriter = null

                val endTime = Instant.now()
                plugin.logger.info("Completed loading chunks in ${Duration.between(startTime, endTime).toMinutes()} minutes.")
            } catch (e: Throwable) {
                plugin.logger.log(Level.WARNING, "Error scanning chunk", e)
            } finally {
                tileEntityWriter?.close()
                entityWriter?.close()
            }
        }, 20 * 5) // Server startup delay
    }
}
