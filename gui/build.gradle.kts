import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
    alias(libs.plugins.shadow)
}

version = "1.2.0"
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
    prefix = "gui"
    description = "gui plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.gui.GuiPlugin"
    depend = listOf("kotlin")
}
