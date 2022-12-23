import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2" // shadowJar
    id("io.papermc.paperweight.userdev") version "1.3.5" // paperweight
}

version = "1.7.21.1"
description = ""

dependencies {
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")

    library("org.jetbrains.kotlin:kotlin-stdlib")
    library("org.jetbrains.kotlin:kotlin-reflect")
    library("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

    library("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.6.0")
    library("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.6.0")

    implementation("xyz.jpenilla:reflection-remapper:0.1.0-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    shadowJar {
        dependencies {
            include(dependency("xyz.jpenilla:reflection-remapper"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    description = "Kotlin dependency plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.kotlin.PluginEntry"
    apiVersion = "1.19"
    prefix = project.name
    authors = listOf("toddharrison")
}
