package com.briarcraft.gui.api

import org.bukkit.entity.Player

fun isUIOpen(player: Player) = player.openInventory is UserInterfaceView
