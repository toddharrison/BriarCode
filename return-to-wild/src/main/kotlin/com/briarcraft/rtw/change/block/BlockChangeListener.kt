package com.briarcraft.rtw.change.block

import com.briarcraft.rtw.change.repo.DependencyChange
import com.briarcraft.rtw.change.repo.DependencyChanges
import com.briarcraft.rtw.util.*
import com.briarcraft.rtw.perm.PermissionService
import com.github.shynixn.mccoroutine.bukkit.ticks
import kotlinx.coroutines.delay
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Levelled
import org.bukkit.block.data.type.Bed
import org.bukkit.block.data.type.Leaves
import org.bukkit.block.data.type.Stairs
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Snowman
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.event.world.StructureGrowEvent
import kotlin.time.Duration.Companion.seconds

class BlockChangeListener(
    private val permService: PermissionService,
    private val repository: BlockChangeRepository2,
    private val config: BlockChangeConfig
): Listener {
    // TODO Prevent vine growth while doing a restore

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockBreakEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val context = event.player.getContext()
        val state = event.block.state
        when (val blockData = state.blockData) {
            is Bisected -> {
                if (blockData !is Stairs) {
                    val otherState = when (blockData.half) {
                        Bisected.Half.TOP -> state.block.getRelative(BlockFace.DOWN).state
                        Bisected.Half.BOTTOM -> state.block.getRelative(BlockFace.UP).state
                    }
                    repository.saveQueued(BlockChange(context, TYPE_BREAK, event.player.type.key, event.player.name, otherState.location, otherState.blockData, newMaterial = Material.AIR))
                }
            }
            is Bed -> {
                val otherState = state.block.getRelative(blockData.facing).state
                repository.saveQueued(BlockChange(context, TYPE_BREAK, event.player.type.key, event.player.name, otherState.location, otherState.blockData, newMaterial = Material.AIR))
            }
        }
        repository.saveQueued(BlockChange(context, TYPE_BREAK, event.player.type.key, event.player.name, state.location, state.blockData, newMaterial = Material.AIR))
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockIgniteEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val context = event.player?.getContext() ?: CONTEXT_ORIGINAL
        val state = event.block.state
        val ignitingState = event.ignitingBlock?.state
        val ignitingEntity = event.ignitingEntity
        when {
            ignitingState != null -> {
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_IGNITE, ignitingState.type.key, null, state.location, state.blockData, newMaterial = Material.FIRE),
                    DependencyChange(ignitingState.location)
                )
            }
            ignitingEntity != null -> {
                if (ignitingEntity is Player) {
                    repository.saveQueued(BlockChange(context, TYPE_IGNITE, ignitingEntity.type.key, ignitingEntity.name, state.location, state.blockData, newMaterial = Material.FIRE))
                } else {
                    repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_IGNITE, ignitingEntity.type.key, null, state.location, state.blockData, newMaterial = Material.FIRE))
                }
            }
            else -> {
                repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_IGNITE, null, null, state.location, state.blockData, newMaterial = Material.FIRE))
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockBurnEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val ignitingState = event.ignitingBlock?.state
        check(ignitingState != null)
        val state = event.block.state
        repository.saveWherePresentQueued(
            BlockChange(CONTEXT_INHERIT, TYPE_BURN, event.ignitingBlock?.type?.key, null, state.location, state.blockData, newMaterial = Material.AIR),
            DependencyChange(ignitingState.location)
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockGrowEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val state = event.block.state
        when (event.newState.type) {
            Material.PUMPKIN,
            Material.MELON,
            Material.SUGAR_CANE,
            Material.CACTUS,
            Material.BAMBOO -> {
                val sourceBlock = state.block.getRelative(BlockFace.DOWN)
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_GROW, sourceBlock.type.key, null, state.location, state.blockData, newMaterial = Material.AIR),
                    DependencyChange(sourceBlock.location)
                )
            }
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.NETHER_WART,
            Material.TURTLE_EGG,
            Material.MELON_STEM,
            Material.PUMPKIN_STEM,
            Material.SWEET_BERRY_BUSH,
            Material.COCOA -> {}
            else -> println("*** UNHANDLED GROW *** " + event.newState.type)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockSpreadEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val state = event.block.state
        val source = event.source.state
        when (source.type) {
            // Don't track grass spread
            Material.GRASS_BLOCK -> {}

            Material.BUDDING_AMETHYST, // TODO Should this be here?
            Material.SMALL_AMETHYST_BUD,
            Material.MEDIUM_AMETHYST_BUD,
            Material.LARGE_AMETHYST_BUD,
            Material.AMETHYST_CLUSTER -> {}

            // Ignore air spread
            Material.AIR,
            Material.VOID_AIR,
            Material.CAVE_AIR -> {}

            Material.FIRE -> {
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_IGNITE, source.type.key, null, state.location, state.blockData, newMaterial = Material.AIR),
                    DependencyChange(source.location)
                )
            }

            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM -> {
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_IGNITE, source.type.key, null, state.location, state.blockData, newMaterial = Material.AIR),
                    DependencyChange(source.location)
                )
            }

            // High-frequency spread events, only track break and place, not grow
            Material.KELP,
            Material.SUGAR_CANE,
            Material.CAVE_VINES,
            Material.WEEPING_VINES,
            Material.TWISTING_VINES,
            Material.BAMBOO,
            Material.BAMBOO_SAPLING -> {}

            // Record all sculk spread for restore
            Material.SCULK_CATALYST -> repository.saveQueued(BlockChange(CONTEXT_INHERIT, TYPE_SPREAD, source.type.key, null, state.location, state.blockData, newMaterial = Material.AIR))

            // Vines can attach to other things, need to track all of them
            Material.VINE -> {
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_SPREAD, source.type.key, null, state.location, state.blockData, newMaterial = Material.AIR),
                    DependencyChange(source.location)
                )
            }

            // Pointed dripstone growing
            Material.POINTED_DRIPSTONE -> {
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_SPREAD, source.type.key, null, state.location, state.blockData, newMaterial = Material.AIR),
                    DependencyChange(source.location)
                )
            }

            else -> {
                when (event.newState.type) {
                    Material.POINTED_DRIPSTONE -> { // Pointed dripstone forming on the floor
                        repository.saveWherePresentQueued(
                            BlockChange(CONTEXT_INHERIT, TYPE_SPREAD, source.type.key, null, state.location, state.blockData, newMaterial = Material.AIR),
                            DependencyChange(findBlockAbove(source.location, Material.POINTED_DRIPSTONE, 12))
                        )
                    }
                    else -> println("*** UNHANDLED SPREAD *** " + event.newState.type)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockFormEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val state = event.block.state
        when (event) {
            is EntityBlockFormEvent -> {
                when (event.newState.type) {
                    Material.SNOW -> {
                        val entity = event.entity
                        if (entity is Snowman) {
                            repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_FORM, entity.type.key, null, state.location, state.blockData, newMaterial = Material.SNOW))
                        } else println("*** UNHANDLED FORM *** " + entity.type.key.asString())
                    }
                    Material.FROSTED_ICE -> {
                        val entity = event.entity
                        if (entity is Player) {
                            val context = entity.getContext()
                            repository.saveQueued(BlockChange(context, TYPE_FORM, entity.type.key, entity.name, state.location, state.blockData, newMaterial = Material.FROSTED_ICE))
                        } else println("*** UNHANDLED FORM *** " + entity.type.key.asString())
                    }
                    Material.WITHER_ROSE -> {
                        repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_FORM, EntityType.WITHER.key, event.entity.type.key.asString(), state.location, state.blockData, newMaterial = Material.WITHER_ROSE))
                    }
                    else -> println("*** UNHANDLED FORM *** " + state.type)
                }
            }
            else -> {
                when (event.newState.type) {
                    Material.SNOW,
                    Material.ICE -> {}
                    Material.COBBLESTONE,
                    Material.STONE,
                    Material.OBSIDIAN -> {
                        val adjacentLocations = getAdjacentTriggeringBlocks(state.block)
                            .filter { block -> block.type == Material.WATER || block.type == Material.LAVA }
                            .map(Block::getLocation)
                        repository.saveWhereOnePresentQueued(
                            BlockChange(CONTEXT_INHERIT, TYPE_FORM, event.block.type.key, null, state.location, state.blockData, newMaterial = event.newState.type),
                            DependencyChanges(state.world, adjacentLocations)
                        )
                    }
                    else -> println("*** UNHANDLED FORM *** " + event.newState.type)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockFadeEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val state = event.block.state
        when (state.type) {
            Material.FIRE -> {}
            Material.FARMLAND -> {
                // TODO
                repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_CHANGE, null, null, state.location, state.blockData, newMaterial = event.newState.type)) // TODO Verify
            }
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.FROSTED_ICE -> {}
            Material.GRASS_BLOCK,
            Material.PODZOL,
            Material.MYCELIUM -> {}
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.DIRT_PATH -> {
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_FADE, state.type.key, null, state.location, state.blockData, newMaterial = event.newState.type),
                    DependencyChange(state.block.getRelative(BlockFace.UP).location)
                )
            }
            Material.SCAFFOLDING -> {}
            Material.BRAIN_CORAL,
            Material.BUBBLE_CORAL,
            Material.FIRE_CORAL,
            Material.HORN_CORAL,
            Material.TUBE_CORAL,
            Material.BRAIN_CORAL_BLOCK,
            Material.BUBBLE_CORAL_BLOCK,
            Material.FIRE_CORAL_BLOCK,
            Material.HORN_CORAL_BLOCK,
            Material.TUBE_CORAL_BLOCK,
            Material.BRAIN_CORAL_FAN,
            Material.BUBBLE_CORAL_FAN,
            Material.FIRE_CORAL_FAN,
            Material.HORN_CORAL_FAN,
            Material.TUBE_CORAL_FAN,
            Material.BRAIN_CORAL_WALL_FAN,
            Material.BUBBLE_CORAL_WALL_FAN,
            Material.FIRE_CORAL_WALL_FAN,
            Material.HORN_CORAL_WALL_FAN,
            Material.TUBE_CORAL_WALL_FAN -> {
                repository.saveWhereOnePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_FADE, state.type.key, null, state.location, state.blockData, newMaterial = event.newState.type),
                    DependencyChanges(state.world, getImmediatelyAdjacentBlockLocations(state.location))
                )
            }
            else -> println("*** UNHANDLED FADE *** " + state.type)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockExplodeEvent) {
        event.blockList()
            .filter { permService.isRecordable(it.location) }
            .map { block -> block.state }
            .map { state -> BlockChange(CONTEXT_INHERIT, TYPE_EXPLODE, event.block.type.key, null, state.location, state.blockData, newMaterial = Material.AIR) }
            .let { changes ->
                repository.saveAllWherePresentQueued(changes, DependencyChange(event.block.location))
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: EntityExplodeEvent) {
        event.blockList()
            .filter { permService.isRecordable(it.location) }
            .map { block -> block.state }
            .mapNotNull { state ->
                when (event.entityType) {
                    EntityType.CREEPER,
                    EntityType.FIREBALL,
                    EntityType.WITHER,
                    EntityType.WITHER_SKULL,
                    EntityType.ENDER_CRYSTAL,
                    EntityType.PRIMED_TNT,
                    EntityType.MINECART_TNT -> {
                        BlockChange(CONTEXT_ORIGINAL, TYPE_EXPLODE, event.entity.type.key, null, state.location, state.blockData, newMaterial = Material.AIR)
                    }
                    else -> {
                        println("*** UNRECOGNIZED ENTITY EXPLOSION *** " + event.entityType)
                        null
                    }
                }
            }
            .let { repository.saveAllQueued(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockFromToEvent) {
        if (!permService.isRecordable(event.toBlock.location)) return

        val state = event.block.state
        val toBlock = event.toBlock.state
        if (toBlock.type.isSolid) {
            repository.saveWherePresentQueued(
                BlockChange(CONTEXT_INHERIT, TYPE_MOVE, state.block.type.key, null, toBlock.location, toBlock.blockData, newMaterial = toBlock.type),
                DependencyChange(state.location)
            )
        } else {
            // Liquid flowing into air blocks
            repository.saveWherePresentQueued(
                BlockChange(CONTEXT_INHERIT, TYPE_FLOW, state.block.type.key, null, toBlock.location, toBlock.blockData, newMaterial = state.location.block.type),
                DependencyChange(state.location)
            )
        }
    }

    // TODO Test Piston movement recording
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockPistonExtendEvent) {
        val direction = event.direction
        event.blocks
            .flatMap { listOf(it.state, it.getRelative(direction).state) }
            .filter { permService.isRecordable(it.location) }
                // TODO Set to real new type
            .map { state -> BlockChange(CONTEXT_INHERIT, TYPE_PISTON, event.block.type.key, null, state.location, state.blockData, newMaterial = Material.AIR) }
            .let { changes ->
                repository.saveAllWherePresentQueued(changes, DependencyChange(event.block.location))
            }
    }

    // TODO Test Piston movement recording
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockPistonRetractEvent) {
        val direction = event.direction
        event.blocks
            .flatMap { listOf(it.state, it.getRelative(direction).state) }
            .filter { permService.isRecordable(it.location) }
                // TODO Set to real new type
            .map { state -> BlockChange(CONTEXT_INHERIT, TYPE_PISTON, event.block.type.key, null, state.location, state.blockData, newMaterial = Material.AIR) }
            .let { changes ->
                repository.saveAllWherePresentQueued(changes, DependencyChange(event.block.location))
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockPlaceEvent) {
        val context = event.player.getContext()
        if (event is BlockMultiPlaceEvent) {
            val changes = event.replacedBlockStates
                .filter { permService.isRecordable(it.location) }
                .map { BlockChange(context, TYPE_PLACE, event.player.type.key, event.player.name, it.location, it.blockData, newMaterial = event.blockPlaced.type) }
            repository.saveAllQueued(changes)
        } else {
            if (!permService.isRecordable(event.blockReplacedState.location)) return

            val state = event.blockReplacedState
            repository.saveQueued(BlockChange(context, TYPE_PLACE, event.player.type.key, event.player.name, state.location, state.blockData, newMaterial = event.blockPlaced.type))
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: SpongeAbsorbEvent) {
        event.blocks
            .filter { permService.isRecordable(it.location) }
            .map { it.location.block.state to it.type }
            .map { (state, newType) -> BlockChange(CONTEXT_INHERIT, TYPE_ABSORB, event.block.type.key, null, state.location, state.blockData, newMaterial = newType) }
            .let { changes ->
                repository.saveAllWherePresentQueued(changes, DependencyChange(event.block.location))
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: EntityChangeBlockEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val state = event.block.state
        when (event.entityType) {
            EntityType.PLAYER -> {
                val player = event.entity as Player
                val context = player.getContext()
                repository.saveQueued(BlockChange(context, TYPE_CHANGE, player.type.key, player.name, state.location, state.blockData, newMaterial = event.to))
            }
            EntityType.SILVERFISH -> {
                repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_INFEST, event.entity.type.key, null, state.location, state.blockData, newMaterial = event.to))
            }
            EntityType.SHEEP -> {
                if (config.recordSheepEat) {
                    val eatState = state.block.getRelative(BlockFace.DOWN).state
                    repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_EAT, event.entity.type.key, null, eatState.location, eatState.blockData, newMaterial = event.to)) // TODO Verify
                }
            }
            EntityType.ENDERMAN -> {
                if (config.recordEndermanTake) {
                    repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_TAKE, event.entity.type.key, null, state.location, state.blockData, newMaterial = event.to)) // TODO Verify
                }
            }
            EntityType.VILLAGER -> {
                if (config.recordVillager) {
                    if (state.type != event.to) {
                        repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_USE, event.entity.type.key, null, state.location, state.blockData, newMaterial = event.to)) // TODO Verify
                    }
                }
            }
            EntityType.FALLING_BLOCK -> {
                if (state.type == Material.REDSTONE_ORE || state.type == Material.DEEPSLATE_REDSTONE_ORE) return

                val origin = event.entity.origin
                if (origin != null) {
                    repository.saveWherePresentQueued(
                        BlockChange(CONTEXT_INHERIT, TYPE_FALL, event.entity.type.key, null, state.location, state.blockData, newMaterial = event.to),
                        DependencyChange(origin)
                    )
                }
            }
            else -> {
                when (state.type) {
                    Material.REDSTONE_ORE,
                    Material.DEEPSLATE_REDSTONE_ORE -> {}

                    Material.BIG_DRIPLEAF -> {}

                    Material.FARMLAND -> {
                        val entity = event.entity
                        if (entity is Player) {
                            val context = entity.getContext()
                            repository.saveQueued(BlockChange(context, TYPE_CHANGE, event.entity.type.key, null, state.location, state.blockData, newMaterial = event.to))
                        } else {
                            repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_CHANGE, event.entity.type.key, null, state.location, state.blockData, newMaterial = event.to)) // TODO Verify
                        }
                    }

                    Material.ACACIA_DOOR,
                    Material.BIRCH_DOOR,
                    Material.CRIMSON_DOOR,
                    Material.DARK_OAK_DOOR,
                    Material.JUNGLE_DOOR,
                    Material.MANGROVE_DOOR,
                    Material.OAK_DOOR,
                    Material.SPRUCE_DOOR,
                    Material.WARPED_DOOR -> {
                        // TODO: Entity breaking door
                    }

                    // TODO: WHEAT

                    else -> println("*** UNRECOGNIZED CHANGE *** " + state.type)
                }
//                if (state.type != event.to) {
//                    repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_CHANGE, event.entity.type.key, null, state.location, state.blockData))
//                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: EntitySpawnEvent) {
        val entity = event.entity
        if (event.entityType == EntityType.PRIMED_TNT) {
            if (!permService.isRecordable(event.entity.location)) return

            repository.saveQueued(BlockChange(CONTEXT_ORIGINAL, TYPE_IGNITE, entity.type.key, null, entity.location, entity.location.block.blockData, newMaterial = Material.AIR))
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: PlayerBucketEmptyEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val context = event.player.getContext()
        val state = event.block.state
        when (event.bucket) {
            Material.WATER_BUCKET -> {
                if (state.type != Material.WATER || (state.blockData as Levelled).level != 0) {
                    repository.saveQueued(BlockChange(context, TYPE_BUCKET, event.player.type.key, event.player.name, state.location, state.blockData, newMaterial = Material.WATER))
                }
            }
            Material.LAVA_BUCKET -> {
                if (state.type != Material.LAVA || (state.blockData as Levelled).level != 0) {
                    repository.saveQueued(BlockChange(context, TYPE_BUCKET, event.player.type.key, event.player.name, state.location, state.blockData, newMaterial = Material.LAVA))
                }
            }
            Material.POWDER_SNOW_BUCKET -> {
                repository.saveQueued(BlockChange(context, TYPE_BUCKET, event.player.type.key, event.player.name, state.location, state.blockData, newMaterial = Material.POWDER_SNOW))
            }
            else -> println("*** UNHANDLED BUCKET EMPTY *** " + event.bucket)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: PlayerBucketFillEvent) {
        if (!permService.isRecordable(event.blockClicked.location)) return

        val context = event.player.getContext()
        val state = event.blockClicked.state
        when (state.type) {
            Material.WATER -> {
                if (getHorizontalAdjacentBlockLocations(state.location)
                        .count { it.block.type == Material.WATER && (it.block.blockData as Levelled).level == 0 } < 2) {
                    repository.saveQueued(BlockChange(context, TYPE_BUCKET, event.player.type.key, event.player.name, state.location, state.blockData, newMaterial = Material.AIR))
                }
            }
            else -> {
                repository.saveQueued(BlockChange(context, TYPE_BUCKET, event.player.type.key, event.player.name, state.location, state.blockData, newMaterial = Material.AIR))
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: PortalCreateEvent) {
        event.blocks
            .filter { permService.isRecordable(it.location) }
            .forEach { state ->
                when (event.reason) {
                    PortalCreateEvent.CreateReason.FIRE -> {
                        if (state.type == Material.NETHER_PORTAL) {
                            val entity = event.entity
                            state.type = Material.AIR
                            if (entity is Player) {
                                val context = entity.getContext()
                                repository.saveQueued(BlockChange(context, TYPE_PORTAL, entity.type.key, entity.name, state.location, state.blockData, newMaterial = Material.NETHER_PORTAL))
                            } else println("*** UNHANDLED PORTAL *** " + entity?.type?.key?.asString())
                        }
                    }
                    PortalCreateEvent.CreateReason.NETHER_PAIR -> {
                        val entity = event.entity
                        state.type = Material.AIR
                        if (entity is Player) {
                            val context = entity.getContext()
                            repository.saveQueued(BlockChange(context, TYPE_PORTAL, entity.type.key, entity.name, state.location, state.blockData, newMaterial = Material.NETHER_PORTAL))
                        } else println("*** UNHANDLED PORTAL *** " + entity?.type?.key?.asString())
                    }
                    PortalCreateEvent.CreateReason.END_PLATFORM -> {}
                }
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: StructureGrowEvent) {
        event.blocks
            .filter { permService.isRecordable(it.location) }
            .map { it.location.block.state to it }
            .forEach { (state, newState) ->
                val player = event.player
                if (player == null) {
                    repository.saveWherePresentQueued(
                        BlockChange(CONTEXT_INHERIT, TYPE_GROW, event.world.key, event.world.name, state.location, state.blockData, newMaterial = newState.type),
                        DependencyChange(event.location)
                    )
                } else {
                    val context = event.player?.getContext() ?: CONTEXT_ORIGINAL
                    repository.saveQueued(BlockChange(context, TYPE_GROW, player.type.key, player.name, state.location, state.blockData, newMaterial = newState.type))
                }
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: BlockFertilizeEvent) {
        val context = event.player?.getContext() ?: CONTEXT_ORIGINAL
        event.blocks
            .filter { permService.isRecordable(it.location) }
            .map { it.location.block.state to it }
            .forEach { (state, newState) ->
                val player = event.player
                if (player == null) {
                    repository.saveWherePresentQueued(
                        BlockChange(CONTEXT_INHERIT, TYPE_FERTILIZE, event.block.type.key, null, state.location, state.blockData, newMaterial = newState.type),
                        DependencyChange(event.block.location)
                    )
                } else {
                    repository.saveQueued(BlockChange(context, TYPE_FERTILIZE, player.type.key, player.name, state.location, state.blockData, newMaterial = newState.type))
                }
            }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun on(event: FluidLevelChangeEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val state = event.block.state
        val adjacentLocations = getImmediatelyAdjacentBlocks(event.block)
            .map { it.location }
        repository.saveWhereOnePresentQueued(
            BlockChange(CONTEXT_INHERIT, TYPE_FLOW, event.block.type.key, null, state.location, state.blockData, newMaterial = event.newData.material),
            DependencyChanges(state.world, adjacentLocations)
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    suspend fun on(event: BlockPhysicsEvent) {
        if (!permService.isRecordable(event.block.location)) return

        val state = event.block.state
        val sourceState = event.sourceBlock.state
        val blockData = event.changedBlockData
        if (blockData is Leaves) {
            delay(10.seconds) // TODO: This has to be longer than the leaf decay setting in tweakin because it cancels physics events
            val newData = state.location.block.blockData
            if (state.blockData != newData) {
                repository.saveWherePresentQueued(
                    BlockChange(CONTEXT_INHERIT, TYPE_PHYSICS, event.sourceBlock.type.key, null, state.location, state.blockData, newMaterial = newData.material),
                    DependencyChange(sourceState.location)
                )
            }
        } else if (sourceState.type != event.changedType) {
            delay(1.ticks) // TODO: Why do we need a delay here?
            val newMaterial = state.location.block.type
            if (state.type != state.location.block.type) {
                if (MATERIAL_ATTACHED.contains(event.changedType)) {
                    repository.saveWherePresentQueued(
                        BlockChange(CONTEXT_INHERIT, TYPE_PHYSICS, event.block.type.key, null, state.location, state.blockData, newMaterial = newMaterial),
                        DependencyChange(sourceState.location)
                    )
                }
            }
        }
    }
}
