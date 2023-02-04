package com.briarcraft.rtw.change.block

data class BlockChangeConfig(
    val recordSheepEat: Boolean = false,
    val recordEndermanTake: Boolean = true,
    val recordVillager: Boolean = false
)
