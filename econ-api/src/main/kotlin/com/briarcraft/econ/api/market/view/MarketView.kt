package com.briarcraft.econ.api.market.view

import com.briarcraft.econ.api.market.Market
import com.briarcraft.econ.api.price.PriceAdjustment
import com.briarcraft.gui.api.ViewUpdateEvent
import com.briarcraft.gui.util.closeUI
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface MarketView {
    val title: Component
    val market: Market
    val priceAdjustment: PriceAdjustment

    fun open(player: Player)

    fun close(player: Player): ViewUpdateEvent? {
        player.closeUI()
        return null
    }
}
