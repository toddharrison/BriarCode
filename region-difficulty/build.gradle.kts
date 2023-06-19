import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
//    alias(libs.plugins.paper.userdev)
    id("io.papermc.paperweight.userdev")
}

version = "2.0.0-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))

    paperweight.paperDevBundle(libs.versions.paper.get())

    compileOnly(libs.reflectionremapper)

    // WorldGuard
    compileOnly(libs.worldguard)
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
