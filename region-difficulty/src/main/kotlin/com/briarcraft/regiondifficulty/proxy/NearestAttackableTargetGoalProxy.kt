package com.briarcraft.regiondifficulty.proxy

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import xyz.jpenilla.reflectionremapper.proxy.annotation.FieldGetter
import xyz.jpenilla.reflectionremapper.proxy.annotation.Proxies

@Proxies(NearestAttackableTargetGoal::class)
interface NearestAttackableTargetGoalProxy {
    @FieldGetter("targetType")
    fun getTargetType(goal: NearestAttackableTargetGoal<*>): Class<*>
}
