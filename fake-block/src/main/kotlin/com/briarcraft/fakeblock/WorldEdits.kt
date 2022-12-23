//package com.briarcraft.fakeblock
//
//import com.sk89q.worldedit.WorldEdit
//import com.sk89q.worldedit.bukkit.BukkitAdapter
//import com.sk89q.worldedit.regions.Region
//import org.bukkit.entity.Player
//
//fun getSelectedRegion(player: Player): Region? {
//    val actor = BukkitAdapter.adapt(player)
//    val manager = WorldEdit.getInstance().sessionManager
//    val localSession = manager.get(actor)
//    return localSession.selectionWorld?.let {
//        localSession.getSelection(it)
//    }
//}
