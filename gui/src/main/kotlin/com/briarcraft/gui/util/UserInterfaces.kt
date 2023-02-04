package com.briarcraft.gui.util

import com.briarcraft.gui.api.UserInterfaceView
import org.bukkit.entity.Player

fun isUIOpen(player: Player) = player.openInventory is UserInterfaceView
