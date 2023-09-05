import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
    alias(libs.plugins.shadow)
}

version = "2.0.0"
description = ""

dependencies {
    api(project(":gui-api"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            include(project(":gui-api"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
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
    prefix = "gui"
    description = "gui plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.gui.GuiPlugin"
    depend = listOf("kotlin")
}
