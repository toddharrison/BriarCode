package com.briarcraft.adventure

import com.briarcraft.adventure.api.registerEnchantments
import com.briarcraft.adventure.enchant.PotionEffectEnchantments
import com.briarcraft.adventure.event.*
import com.briarcraft.adventure.item.CustomItems
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import org.bukkit.event.HandlerList

@Suppress("unused")
class AdventurePlugin: SuspendingJavaPlugin() {
    private lateinit var adventureCommand: AdventureCommand

    override suspend fun onLoadAsync() {
        saveDefaultConfig()
    }

    override suspend fun onEnableAsync() {
        val adventureEnchants = PotionEffectEnchantments(this)
        registerEnchantments(*adventureEnchants.enchantments.toTypedArray())

        server.pluginManager.registerSuspendingEvents(PotionEffectEnchantListener(), this)

        val customItems = CustomItems(this, adventureEnchants)

        val anvilListener = CustomEnchantListener()
        server.pluginManager.registerSuspendingEvents(anvilListener, this)

        val lootChest = RegenerateLootEventListener(this)
        server.pluginManager.registerSuspendingEvents(lootChest, this)

        adventureCommand = AdventureCommand(customItems)
        adventureCommand.register()
    }

    override suspend fun onDisableAsync() {
        adventureCommand.unregister()

        HandlerList.unregisterAll(this)
    }
}
