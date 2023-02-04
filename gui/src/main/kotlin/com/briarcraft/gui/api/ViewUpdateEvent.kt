package com.briarcraft.gui.api

data class ViewUpdateEvent(
    val top: PanelUpdateEvent? = null,
    val mid: PanelUpdateEvent? = null,
    val nav: PanelUpdateEvent? = null
)

data class PanelUpdateEvent(
    val slots: Set<Int>? = null
)
