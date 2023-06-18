import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
    alias(libs.plugins.shadow)
}

version = "1.3.0"
description = ""

dependencies {
    api(project(":adventure-api"))

    // CommandAPI
    implementation(libs.commandapi)
    annotationProcessor(libs.commandapi)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            include(project(":adventure-api"))
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
    prefix = "adventure"
    description = "Adventure plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.adventure.AdventurePlugin"
    depend = listOf("kotlin", "CommandAPI")
}
