import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "1.5.2"
description = ""

dependencies {
    api(project(":kotlin"))

    // WorldGuard / WorldEdit
    implementation("com.sk89q.worldguard", "worldguard-bukkit", "7.0.7")
    implementation("com.sk89q.worldedit", "worldedit-bukkit", "7.2.10")

    // Protection Stones
    implementation("dev.espi", "protectionstones", "2.10.2") // 2.10.3

    // CommandAPI
    implementation("dev.jorel", "commandapi-annotations", "8.7.4")
    annotationProcessor("dev.jorel", "commandapi-annotations", "8.7.4")

    api(project(":data-source"))
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
    prefix = "return-to-wild"
    description = "return-to-wild plugin"
    load = PluginLoadOrder.POSTWORLD
    main = "com.briarcraft.rtw.ReturnToWildPlugin"
    depend = listOf("kotlin", "data-source", "CommandAPI")
    softDepend = listOf("WorldGuard", "ProtectionStones")
}
