package com.briarcraft.regiondifficulty

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntityShootBowEvent

@Suppress("unused")
class RegionDifficultyListener(private val plugin: RegionDifficultyPlugin, private val nerfer: MonsterNerfer, private val config: RegionDifficultyConfig) : Listener {
    private fun shouldNerf(monster: Monster) =
        monster.entitySpawnReason != CreatureSpawnEvent.SpawnReason.SPAWNER
            && config.worldsToNerf.contains(monster.world.name)

    @EventHandler(priority = EventPriority.LOWEST)
    fun onEntitySpawn(event: EntityAddToWorldEvent) {
        val entity = event.entity
        if (entity is Monster && shouldNerf(entity)) {
            nerfer.nerfMonster(entity)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onEntityShootBow(event: EntityShootBowEvent) {
        val entity = event.entity
        if (entity is Monster && shouldNerf(entity)) {
            if (entity is Skeleton) {
                when (nerfer.getMonsterDifficulty(entity)) {
                    RegionDifficulty.FRIENDLY -> {
                        (event.projectile as Arrow).damage = 2.0
                        (event.projectile as Arrow).fireTicks = 0
                    }
                    RegionDifficulty.EASY -> {
                        (event.projectile as Arrow).damage = 2.0
                        (event.projectile as Arrow).fireTicks = 0
                    }
                    RegionDifficulty.NORMAL -> {
                        (event.projectile as Arrow).damage = 3.5
                        (event.projectile as Arrow).fireTicks = 0
                    }
                    RegionDifficulty.HARD -> {}
                }
            } else if (entity is Pillager) {
                when (nerfer.getMonsterDifficulty(entity)) {
                    RegionDifficulty.FRIENDLY -> {
                        (event.projectile as Arrow).damage = 2.0
                    }
                    RegionDifficulty.EASY -> {
                        (event.projectile as Arrow).damage = 2.0
                    }
                    RegionDifficulty.NORMAL -> {
                        (event.projectile as Arrow).damage = 3.5
                    }
                    RegionDifficulty.HARD -> {}
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        if (event.action == EntityPotionEffectEvent.Action.ADDED) {
            when (event.cause) {
                EntityPotionEffectEvent.Cause.SPIDER_SPAWN -> {
                    // Cancel spider spawn potion effects
                    val entity = event.entity as Spider
                    if (shouldNerf(entity)) {
                        when (nerfer.getMonsterDifficulty(entity)) {
                            RegionDifficulty.FRIENDLY -> {
                                event.isCancelled = true
                            }
                            RegionDifficulty.EASY -> {
                                event.isCancelled = true
                            }
                            RegionDifficulty.NORMAL -> {
                                event.isCancelled = true
                            }
                            RegionDifficulty.HARD -> {}
                        }
                    }
                }
                EntityPotionEffectEvent.Cause.ATTACK -> {
                    val entity = event.entity
                    if (entity is Player) {
                        val attacker = entity.lastDamageCause?.entity
                        if (attacker is Monster && shouldNerf(attacker)) {
                            when (attacker) {
                                is CaveSpider -> {
                                    when (nerfer.getMonsterDifficulty(attacker)) {
                                        RegionDifficulty.FRIENDLY -> {
                                            event.isCancelled = true
                                        }
                                        RegionDifficulty.EASY -> {
                                            event.isCancelled = true
                                        }
                                        RegionDifficulty.NORMAL -> {
                                            // Reduce poison effect on players from cave spiders for normal
                                            event.newEffect?.withDuration(150)?.let { // per difficulty
                                                entity.addPotionEffect(it)
                                                event.isCancelled = true
                                            }
                                        }
                                        RegionDifficulty.HARD -> {}
                                    }
                                }
                                is Player -> {}
                                else -> {
                                    plugin.logger.warning(
                                        "Unrecognized potion event: player=${entity.name} attacker=${attacker.type} effect=${event.newEffect?.type}"
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
