import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "2.0.0-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))
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
    prefix = "region-scan"
    description = "region-scan plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.regionscan.RegionScanPlugin"
    depend = listOf("kotlin")
}
