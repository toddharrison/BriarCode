import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
    alias(libs.plugins.shadow)
}

version = "1.5.0"
description = ""

dependencies {
    api(project(":kotlin"))
    api(project(":econ-api"))

    // Vault
    implementation(libs.vault)

    // CommandAPI
    implementation(libs.commandapi)
    annotationProcessor(libs.commandapi)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            include(project(":econ-api"))
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
    prefix = "econ"
    description = "econ plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.econ.EconPlugin"
    depend = listOf("kotlin", "CommandAPI", "gui")
    softDepend = listOf("Vault")
}
