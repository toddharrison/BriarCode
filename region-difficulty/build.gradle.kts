import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
    id("io.papermc.paperweight.userdev")
}

version = "1.1.1"
description = ""

dependencies {
    api(project(":kotlin"))

    paperDevBundle("1.19.4-R0.1-SNAPSHOT")

    compileOnly("xyz.jpenilla:reflection-remapper:0.1.0-SNAPSHOT")

    // WorldGuard
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.7")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

pitest {
    mutationThreshold.set(0)
    coverageThreshold.set(0)
}

kover {
    verify {
        rule {
            bound {
                minValue = 0
                counter = kotlinx.kover.api.CounterType.INSTRUCTION
                valueType = kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
}

bukkit {
    prefix = "region-difficulty"
    description = "region-difficulty plugin"
    load = PluginLoadOrder.POSTWORLD
    main = "com.briarcraft.regiondifficulty.RegionDifficultyPlugin"
    depend = listOf("kotlin", "WorldGuard")
}
