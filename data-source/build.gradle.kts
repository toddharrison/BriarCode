import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "2.0.0"
description = ""

dependencies {
    api(project(":kotlin"))

    library(libs.hikari)
    library(libs.slf4j)
    library(libs.mysql)
    library(libs.mariadb)
    library(libs.h2)

    testImplementation(libs.hikari)
    testImplementation(libs.h2)
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
    prefix = "data-source"
    description = "data-source plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.datasource.DataSourcePlugin"
    depend = listOf("kotlin")
}
