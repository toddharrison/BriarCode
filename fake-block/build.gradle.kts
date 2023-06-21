import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission

plugins {
    id("briarcode.java-plugin")
    alias(libs.plugins.shadow)
}

version = "3.0.1-SNAPSHOT"
description = ""

dependencies {
    api(project(":fake-block-api"))

    implementation(libs.log4j)
    implementation(libs.protocollib)
    implementation(libs.worldedit)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            include(project(":fake-block-api"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = BigDecimal(0.0)
                }
            }
        }
    }
}

pitest {
    mutationThreshold.set(1)
    coverageThreshold.set(1)
}

bukkit {
    apiVersion = "1.17"
    prefix = "fake-block"
    description = "FakeBlock plugin"
    load = PluginLoadOrder.POSTWORLD
    main = "com.briarcraft.fakeblock.FakeBlockPlugin"
    depend = listOf("WorldEdit", "ProtocolLib")
    softDepend = listOf("My_Worlds", "Multiverse-Core")
    commands {
        register("fakeblock") {
            description = "Execute fakeblock commands"
            aliases = listOf("fb")
            permission = "fakeblock"
            permissionMessage = "You may not use fakeblock commands!"
        }
    }
    permissions {
        register("fakeblock") {
            description = "Allows you to run the fakeblock commands"
            default = Permission.Default.OP
        }
    }
}
