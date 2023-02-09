import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "1.0.0-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))

    // CommandAPI
    implementation("dev.jorel", "commandapi-annotations", "8.7.4")
    annotationProcessor("dev.jorel", "commandapi-annotations", "8.7.4")
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
    prefix = "get-ahead"
    description = "get-ahead plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.ahead.AheadPlugin"
    depend = listOf("kotlin", "CommandAPI")
}
