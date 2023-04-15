package com.briarcraft.rtw.restore

import com.briarcraft.rtw.change.block.BlockChangeRepository
import com.briarcraft.rtw.change.block.BlockChangeRepository2
import com.briarcraft.rtw.perm.PermissionService
import com.briarcraft.rtw.util.AtomicToggle
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

interface Restorer {
    val plugin: SuspendingJavaPlugin
    val blockChangeRepo: BlockChangeRepository2
    val permService: PermissionService
    val pauseFlag: AtomicToggle

    suspend fun start()
}
