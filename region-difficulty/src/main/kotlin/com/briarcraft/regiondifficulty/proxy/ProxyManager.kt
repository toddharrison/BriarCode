package com.briarcraft.regiondifficulty.proxy

import xyz.jpenilla.reflectionremapper.ReflectionRemapper
import xyz.jpenilla.reflectionremapper.proxy.ReflectionProxyFactory

object ProxyManager {
    val nearestAttackableTargetGoalProxy: NearestAttackableTargetGoalProxy

    init {
        // Generate the remapper, then allow garbage collection since it can be large
        val reflectionRemapper = ReflectionRemapper.forReobfMappingsInPaperJar()
        val reflectionProxyFactory = ReflectionProxyFactory.create(
            reflectionRemapper,
            ProxyManager::class.java.classLoader
        )

        // Persist the generated proxies
        nearestAttackableTargetGoalProxy = reflectionProxyFactory.reflectionProxy(NearestAttackableTargetGoalProxy::class.java)
    }
}
