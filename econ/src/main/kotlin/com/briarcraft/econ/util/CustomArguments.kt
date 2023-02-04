//package com.briarcraft.econ.util
//
//import dev.jorel.commandapi.arguments.Argument
//import dev.jorel.commandapi.arguments.CustomArgument
//import org.bukkit.Bukkit
//import org.bukkit.Material
//
//fun offlinePlayerArgument(nodeName: String = "player"): Argument =
//    CustomArgument(nodeName) { playerArg ->
//        val name = playerArg.input
//        Bukkit.getOfflinePlayerIfCached(name)
//            ?: Bukkit.getPlayer(name)
//            ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That player does not exist"))
//    }.replaceSuggestions { _ ->
//        (Bukkit.getOnlinePlayers().map { it.name }.toSet() + Bukkit.getOfflinePlayers().map { it.name }.toSet())
//            .filterNotNull()
//            .toTypedArray()
//    }
//
//fun offlinePlayerNotSelfArgument(nodeName: String = "player"): Argument =
//    CustomArgument(nodeName) { playerArg ->
//        val name = playerArg.input
//        Bukkit.getOfflinePlayerIfCached(name)
//            ?: Bukkit.getPlayer(name)
//            ?: throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That player does not exist"))
//    }.replaceSuggestions { info ->
//        (Bukkit.getOnlinePlayers()
//            .map { it.name }
//            .toSet() + Bukkit.getOfflinePlayers().map { it.name }.toSet())
//            .filterNotNull()
//            .filter { it != info.sender.name }
//            .toTypedArray()
//    }
//
//fun itemArgument(items: Set<Material>, nodeName: String = "item"): Argument =
//    CustomArgument(nodeName) { itemArg ->
//        val name = itemArg.input
//        val material = Material.getMaterial(name)
//        if (material != null && items.contains(material)) material
//        else throw CustomArgument.CustomArgumentException(CustomArgument.MessageBuilder("That item does not exist"))
//    }.replaceSuggestions {
//        items
//            .map(Material::name)
//            .toTypedArray()
//    }
