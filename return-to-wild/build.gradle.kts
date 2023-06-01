import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "1.7.0"
description = ""

dependencies {
    api(project(":kotlin"))

    // WorldGuard / WorldEdit
    implementation("com.sk89q.worldguard", "worldguard-bukkit", "7.0.7")
    implementation("com.sk89q.worldedit", "worldedit-bukkit", "7.2.10")

    // Protection Stones
    implementation("dev.espi", "protectionstones", "2.10.2") // 2.10.3

    // CommandAPI
    implementation("dev.jorel", "commandapi-annotations", "9.0.0")
    annotationProcessor("dev.jorel", "commandapi-annotations", "9.0.0")

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
//    permissions {
//        register("return_to_wild.cmd.publicbuild.*") {
//            description = "Gives the user full access to the publicbuild commands"
//            children = listOf(
//                "return_to_wild.cmd.publicbuild.on",
//                "return_to_wild.cmd.publicbuild.off",
//                "return_to_wild.cmd.publicbuild.info",
//                "return_to_wild.cmd.publicbuild.usage"
//            )
//        }
//        register("return_to_wild.cmd.admin.publicbuild.*") {
//            description = "Gives the user full access to the publicbuild admin commands"
//            children = listOf(
//                "return_to_wild.cmd.admin.publicbuild.on",
//                "return_to_wild.cmd.admin.publicbuild.off",
//                "return_to_wild.cmd.admin.publicbuild.info",
//                "return_to_wild.cmd.admin.publicbuild.set"
//            )
//        }
//    }
}
