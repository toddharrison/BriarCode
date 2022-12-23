package com.briarcraft.kotlin.util

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer

fun BukkitScheduler.runTaskLater(plugin: Plugin, delay: Long, task: Runnable) =
    runTaskLater(plugin, task, delay)
fun BukkitScheduler.runTaskLater(plugin: Plugin, delay: Long, task: Consumer<BukkitTask>) =
    runTaskLater(plugin, task, delay)

fun BukkitScheduler.runTaskLaterAsynchronously(plugin: Plugin, delay: Long, task: Runnable) =
    runTaskLaterAsynchronously(plugin, task, delay)
fun BukkitScheduler.runTaskLaterAsynchronously(plugin: Plugin, delay: Long, task: Consumer<BukkitTask>) =
    runTaskLaterAsynchronously(plugin, task, delay)

fun BukkitScheduler.runTaskTimer(plugin: Plugin, delay: Long, period: Long, task: Runnable) =
    runTaskTimer(plugin, task, delay, period)
fun BukkitScheduler.runTaskTimer(plugin: Plugin, delay: Long, period: Long, task: Consumer<BukkitTask>) =
    runTaskTimer(plugin, task, delay, period)

fun BukkitScheduler.runTaskTimerAsynchronously(plugin: Plugin, delay: Long, period: Long, task: Runnable) =
    runTaskTimerAsynchronously(plugin, task, delay, period)
fun BukkitScheduler.runTaskTimerAsynchronously(plugin: Plugin, delay: Long, period: Long, task: Consumer<BukkitTask>) =
    runTaskTimerAsynchronously(plugin, task, delay, period)
