package com.briarcraft.rtw.perm

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.RegionQuery
import org.bukkit.Location

private const val RTW_RECORD = "rtw-record"
private const val RTW_RESTORE = "rtw-restore"

class WorldGuardService: PermissionService {
    private lateinit var worldGuard: WorldGuard
    private lateinit var rtwRecordFlag: StateFlag
    private lateinit var rtwRestoreFlag: StateFlag
    private lateinit var query: RegionQuery

    override fun load() {
        worldGuard = WorldGuard.getInstance()
        rtwRecordFlag = registerWorldGuardFlag(worldGuard, RTW_RECORD, false)
        rtwRestoreFlag = registerWorldGuardFlag(worldGuard, RTW_RESTORE, false)
    }

    override fun enable() {
        query = worldGuard.platform.regionContainer.createQuery()
    }

    override fun isRecordable(location: Location): Boolean {
        return query.testState(BukkitAdapter.adapt(location), null, rtwRecordFlag)
    }

    override fun isRestorable(location: Location): Boolean {
        return query.testState(BukkitAdapter.adapt(location), null, rtwRestoreFlag)
    }

    private fun registerWorldGuardFlag(worldGuard: WorldGuard, name: String, default: Boolean): StateFlag {
        val registry = worldGuard.flagRegistry
        val flag = StateFlag(name, default)
        registry.register(flag)
        return flag
    }
}
