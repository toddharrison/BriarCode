package com.briarcraft.gui.api

import com.briarcraft.gui.api.UserInterfaceView
import org.bukkit.entity.Player

fun isUIOpen(player: Player) = player.openInventory is UserInterfaceView
