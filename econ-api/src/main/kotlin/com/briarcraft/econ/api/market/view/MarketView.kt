package com.briarcraft.econ.api.market.view

import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.gui.api.ViewUpdateEvent
import com.briarcraft.gui.api.closeUI
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

interface MarketView {
    val plugin: Plugin
    val title: Component
    val market: Market
    val priceAdjustment: PriceAdjustment

    fun open(player: Player)

    fun close(player: Player): ViewUpdateEvent? {
        player.closeUI(plugin)
        return null
    }
}
