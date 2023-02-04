package com.briarcraft.regiondifficulty

import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.EnumFlag
import org.bukkit.NamespacedKey
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class RegionDifficultyPlugin : JavaPlugin() {
    private val difficultyKey = NamespacedKey(this, com.briarcraft.regiondifficulty.difficultyKey)
    private val flag = EnumFlag(com.briarcraft.regiondifficulty.difficultyKey, RegionDifficulty::class.java)

    private lateinit var worldGuard: WorldGuard
    private lateinit var nerfer: MonsterNerfer

    override fun onLoad() {
        saveDefaultConfig()

        // Register WorldGuard custom flag
        worldGuard = WorldGuard.getInstance()
        worldGuard.flagRegistry.register(flag)
    }

    override fun onEnable() {
        val rdConfig = loadConfig()

        nerfer = MonsterNerfer(worldGuard, difficultyKey, flag)
        server.pluginManager.registerEvents(RegionDifficultyListener(this, nerfer, rdConfig), this)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
    }

    private fun loadConfig() = RegionDifficultyConfig(
        config.getStringList("worlds-to-nerf")
    )
}
