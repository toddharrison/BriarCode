import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission

plugins {
    id("briarcode.java-plugin")
    id("com.github.johnrengelman.shadow")
}

version = "2.1.0"
description = ""

dependencies {
    api(project(":fake-block-api"))

    implementation("org.apache.logging.log4j", "log4j-core", "2.17.2")
    implementation("com.comphenix.protocol", "ProtocolLib", "5.0.0-SNAPSHOT")
    implementation("com.sk89q.worldedit", "worldedit-bukkit", "7.2.10")
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
