package com.briarcraft.rtw.perm

import org.bukkit.Location

interface PermissionService {
    fun load()
    fun enable()
    fun isRecordable(location: Location): Boolean
    fun isRestorable(location: Location): Boolean
}
