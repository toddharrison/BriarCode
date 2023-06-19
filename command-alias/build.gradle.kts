import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "2.0.0-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))

    // CommandAPI
    implementation(libs.commandapi)
    annotationProcessor(libs.commandapi)

    // LuckPerms
    implementation(libs.luckperms)
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
    prefix = "command-alias"
    description = "command-alias plugin"
    load = PluginLoadOrder.POSTWORLD
    main = "com.briarcraft.commandalias.CommandAliasPlugin"
    depend = listOf("kotlin", "CommandAPI", "LuckPerms")
}
