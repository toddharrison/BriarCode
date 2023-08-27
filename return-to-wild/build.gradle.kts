import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "2.0.0-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))

    // WorldGuard / WorldEdit
    implementation(libs.worldguard)
    implementation(libs.worldedit)

    // Protection Stones
    implementation(libs.protectionstones)

    // CommandAPI
    implementation(libs.commandapi)
    annotationProcessor(libs.commandapi)

    api(project(":data-source"))
}

pitest {
    mutationThreshold.set(0)
    coverageThreshold.set(0)
}

koverReport {
    verify {
        rule {
            bound {
                minValue = 0
                metric = kotlinx.kover.gradle.plugin.dsl.MetricType.INSTRUCTION
                aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
            }
        }
    }
}

bukkit {
    prefix = "return-to-wild"
    description = "return-to-wild plugin"
    load = PluginLoadOrder.POSTWORLD
    main = "com.briarcraft.rtw.ReturnToWildPlugin"
    depend = listOf("kotlin", "data-source", "CommandAPI")
    softDepend = listOf("WorldGuard", "ProtectionStones")
}
