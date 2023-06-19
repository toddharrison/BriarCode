import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "2.0.0-SNAPSHOT"
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
    prefix = "data-source"
    description = "data-source plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.datasource.DataSourcePlugin"
    depend = listOf("kotlin")
}
