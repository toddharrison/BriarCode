package com.briarcraft.rtw.perm

import org.bukkit.Location

class AllPermissionService: PermissionService {
    override fun load() {}

    override fun enable() {}

    override fun isRecordable(location: Location) = true

    override fun isRestorable(location: Location) = true
}
