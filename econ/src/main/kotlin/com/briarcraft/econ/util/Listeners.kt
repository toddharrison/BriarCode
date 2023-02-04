//package com.briarcraft.econ.util
//
//import com.destroystokyo.paper.event.server.ServerTickEndEvent
//import com.destroystokyo.paper.event.server.ServerTickStartEvent
//import org.bukkit.event.Event
//import org.bukkit.event.EventPriority
//import org.bukkit.event.HandlerList
//import org.bukkit.event.Listener
//import org.bukkit.plugin.EventExecutor
//import org.bukkit.plugin.RegisteredListener
//import org.bukkit.plugin.java.JavaPlugin
//
//fun listenForAllEvents(plugin: JavaPlugin) {
//    // Listen to all events
//    val registeredListener = RegisteredListener(
//        object: Listener {},
//        EventExecutor { _: Listener, event: Event ->
//            when (event) {
//                is ServerTickStartEvent,
//                is ServerTickEndEvent,
//                -> {}
//                else -> println(event)
//            }
//        },
//        EventPriority.NORMAL,
//        plugin,
//        false
//    )
//    for (handler in HandlerList.getHandlerLists()) handler.register(registeredListener)
//}
