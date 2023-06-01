package com.briarcraft.rtw.publicbuild

import com.briarcraft.rtw.util.*
import org.bukkit.entity.Player

class PublicBuildService {
    private val publicBuildContext = "PUBLIC"

    fun toggleOn(player: Player) {
        player.setContext(publicBuildContext)
    }

    fun toggleOff(player: Player): Boolean {
        return if (isPublicBuild(player)) {
            player.clearContext()
            true
        } else false
    }

    fun isPublicBuild(player: Player): Boolean {
        return player.getContext() == publicBuildContext
    }

    fun getCurrentCount(player: Player): Int {
        return 0
    }

    fun getMaxCount(player: Player): Int? {
        return player.getMaxAllowance()
    }

    fun setAllowances(player: Player, maxAllowance: Int?): Int? {
        require(maxAllowance == null || maxAllowance >= 0)
        return player.setMaxAllowance(maxAllowance)
    }
}
