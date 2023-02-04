package com.briarcraft.regiondifficulty

import com.briarcraft.regiondifficulty.proxy.ProxyManager
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.EnumFlag
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.ai.goal.WrappedGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.player.Player
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_19_R2.entity.*
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.random.Random

class MonsterNerfer(
    private val worldGuard: WorldGuard,
    private val difficultyKey: NamespacedKey,
    private val flag: EnumFlag<RegionDifficulty>
) {
    private val air = ItemStack(Material.AIR)
    private val oddsPickupItem = 1.0 / 8.0
    private val oddsRemoveItem = 4.0 / 5.0
    private val oddsDisenchantItem = 1.0 / 8.0
    private val oddsRemoveArmor = 1.0 / 8.0
    private val oddsDisenchantArmor = 1.0 / 8.0

    fun getMonsterDifficulty(monster: Monster) = monster.persistentDataContainer
        .get(difficultyKey, PersistentDataType.STRING)
        ?.let(RegionDifficulty::valueOf)
        .let { difficulty ->
            difficulty ?: getLocationDifficulty(monster.location).also {
                monster.persistentDataContainer.set(difficultyKey, PersistentDataType.STRING, it.name)
                nerfEquipment(monster, it)
            }
        }

    fun nerfMonster(entity: Monster) {
        when (entity.type) {
            EntityType.ZOMBIE -> {
                (entity as Zombie).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                            (entity as CraftZombie).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.NORMAL -> {
                            if (Random.nextBoolean(oddsPickupItem)) it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 3.0
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.DROWNED -> {
                (entity as Drowned).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                            (entity as CraftDrowned).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.NORMAL -> {
                            if (Random.nextBoolean(oddsPickupItem)) it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 3.0
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.HUSK -> {
                (entity as Husk).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                            (entity as CraftHusk).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.NORMAL -> {
                            if (Random.nextBoolean(oddsPickupItem)) it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 3.0
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.ZOMBIE_VILLAGER -> {
                (entity as ZombieVillager).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                            (entity as CraftZombie).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.NORMAL -> {
                            if (Random.nextBoolean(oddsPickupItem)) it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 3.0
                            it.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS)?.baseValue = 0.0
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.SKELETON -> {
                (entity as Skeleton).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                            (entity as CraftSkeleton).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                        }
                        RegionDifficulty.NORMAL -> {
                            if (Random.nextBoolean(oddsPickupItem)) it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.STRAY -> {
                (entity as Stray).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                            (entity as CraftStray).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                        }
                        RegionDifficulty.NORMAL -> {
                            if (Random.nextBoolean(oddsPickupItem)) it.canPickupItems = false
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.5
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.CREEPER -> {
                (entity as Creeper).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.explosionRadius = 3
                            // Damage 22.5
                            (entity as CraftCreeper).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.explosionRadius = 3
                            // Damage 22.5
                        }
                        RegionDifficulty.NORMAL -> {
                            it.explosionRadius = 4
                            // Damage 43
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.SPIDER -> {
                (entity as Spider).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                            (entity as CraftSpider).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                        }
                        RegionDifficulty.NORMAL -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.CAVE_SPIDER -> {
                (entity as CaveSpider).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                            (entity as CraftCaveSpider).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                        }
                        RegionDifficulty.NORMAL -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 2.0
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            EntityType.ENDERMAN -> {
                (entity as Enderman).let {
                    when (getMonsterDifficulty(it)) {
                        RegionDifficulty.FRIENDLY -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 4.5
                            (entity as CraftEnderman).handle.let { entity ->
                                entity.targetSelector.let { targetSelector ->
                                    targetSelector.availableGoals
                                        .map(WrappedGoal::getGoal)
                                        .filterIsInstance<NearestAttackableTargetGoal<*>>()
                                        .filter { goal -> ProxyManager.nearestAttackableTargetGoalProxy.getTargetType(goal) == Player::class.java }
                                        .forEach(targetSelector::removeGoal)
                                }
                            }

                            it.customName(Component.text("Friendly"))
                            it.isCustomNameVisible = true
                        }
                        RegionDifficulty.EASY -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 4.5
                        }
                        RegionDifficulty.NORMAL -> {
                            it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = 7.0
                        }
                        RegionDifficulty.HARD -> {}
                    }
                }
            }
            else -> {}
        }
    }

    private fun getLocationDifficulty(
        loc: Location,
        default: RegionDifficulty = RegionDifficulty.HARD
    ): RegionDifficulty {
        val container = worldGuard.platform.regionContainer
        val query = container.createQuery()
        val difficulties = query.queryAllValues(BukkitAdapter.adapt(loc), null, flag)
        return when {
            difficulties.contains(RegionDifficulty.FRIENDLY) -> RegionDifficulty.FRIENDLY
            difficulties.contains(RegionDifficulty.EASY) -> RegionDifficulty.EASY
            difficulties.contains(RegionDifficulty.NORMAL) -> RegionDifficulty.NORMAL
            difficulties.contains(RegionDifficulty.HARD) -> RegionDifficulty.HARD
            else -> default
        }
    }

    private fun nerfEquipment(entity: Monster, difficulty: RegionDifficulty) {
        entity.equipment.let { equipment ->
            when (difficulty) {
                RegionDifficulty.FRIENDLY,
                RegionDifficulty.EASY -> {
                    // Nerf item in main hand
                    when (equipment.itemInMainHand.type) {
                        Material.BOW -> {
                            equipment.setItemInMainHand(ItemStack(Material.BOW))
                        }
                        Material.IRON_SWORD -> {
                            if (Random.nextBoolean(oddsRemoveItem)) equipment.setItemInMainHand(air)
                            else equipment.setItemInMainHand(ItemStack(Material.IRON_SWORD))
                        }
                        Material.IRON_SHOVEL -> {
                            if (Random.nextBoolean(oddsRemoveItem)) equipment.setItemInMainHand(air)
                            else equipment.setItemInMainHand(ItemStack(Material.IRON_SHOVEL))
                        }
                        Material.TRIDENT -> {
                            equipment.setItemInMainHand(air)
                        }
                        else -> {}
                    }

                    // Nerf armor
                    equipment.helmet = null
                    equipment.chestplate = null
                    equipment.leggings = null
                    equipment.boots = null
                }
                RegionDifficulty.NORMAL -> {
                    // Nerf item in main hand
                    when (equipment.itemInMainHand.type) {
                        Material.BOW -> {
                            if (Random.nextBoolean(oddsDisenchantItem)) equipment.setItemInMainHand(ItemStack(Material.BOW))
                        }
                        Material.IRON_SWORD -> {
                            if (Random.nextBoolean(oddsRemoveItem)) equipment.setItemInMainHand(air)
                            else if (Random.nextBoolean(oddsDisenchantItem)) equipment.setItemInMainHand(
                                ItemStack(
                                    Material.IRON_SWORD
                                )
                            )
                        }
                        Material.IRON_SHOVEL -> {
                            if (Random.nextBoolean(oddsRemoveItem)) equipment.setItemInMainHand(air)
                            else if (Random.nextBoolean(oddsDisenchantItem)) equipment.setItemInMainHand(
                                ItemStack(
                                    Material.IRON_SHOVEL
                                )
                            )
                        }
                        Material.TRIDENT -> {
                            if (Random.nextBoolean(oddsRemoveItem)) equipment.setItemInMainHand(air)
                            else if (Random.nextBoolean(oddsDisenchantItem)) equipment.setItemInMainHand(
                                ItemStack(
                                    Material.TRIDENT
                                )
                            )
                        }
                        else -> {}
                    }

                    // Nerf armor
                    if (Random.nextBoolean(oddsRemoveArmor)) {
                        equipment.helmet = null
                        equipment.chestplate = null
                        equipment.leggings = null
                        equipment.boots = null
                    } else {
                        when {
                            equipment.helmet == null -> {}
                            equipment.chestplate == null -> {
                                if (Random.nextBoolean(oddsDisenchantArmor)) {
                                    equipment.helmet = ItemStack(equipment.helmet?.type ?: Material.AIR)
                                }
                            }
                            equipment.leggings == null -> {
                                if (Random.nextBoolean(oddsDisenchantArmor)) {
                                    equipment.helmet = ItemStack(equipment.helmet?.type ?: Material.AIR)
                                    equipment.chestplate = ItemStack(equipment.chestplate?.type ?: Material.AIR)
                                }
                            }
                            equipment.boots == null -> {
                                if (Random.nextBoolean(oddsDisenchantArmor)) {
                                    equipment.helmet = ItemStack(equipment.helmet?.type ?: Material.AIR)
                                    equipment.chestplate = ItemStack(equipment.chestplate?.type ?: Material.AIR)
                                    equipment.leggings = ItemStack(equipment.leggings?.type ?: Material.AIR)
                                }
                            }
                            else -> {
                                if (Random.nextBoolean(oddsDisenchantArmor)) {
                                    equipment.helmet = ItemStack(equipment.helmet?.type ?: Material.AIR)
                                    equipment.chestplate = ItemStack(equipment.chestplate?.type ?: Material.AIR)
                                    equipment.leggings = ItemStack(equipment.leggings?.type ?: Material.AIR)
                                    equipment.boots = ItemStack(equipment.boots?.type ?: Material.AIR)
                                }
                            }
                        }
                    }
                }
                RegionDifficulty.HARD -> {}
            }
        }
    }
}
