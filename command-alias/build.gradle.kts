import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "1.2.0"
description = ""

dependencies {
    api(project(":kotlin"))

    // CommandAPI
    implementation("dev.jorel", "commandapi-annotations", "9.0.0")
    annotationProcessor("dev.jorel", "commandapi-annotations", "9.0.0")

    // LuckPerms
    implementation("net.luckperms:api:5.4")
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
