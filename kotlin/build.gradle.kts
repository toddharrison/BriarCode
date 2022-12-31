import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import kotlinx.kover.api.CounterType
import kotlinx.kover.api.VerificationValueType

plugins {
    id("briarcode.kotlin-plugin")
    id("com.github.johnrengelman.shadow")
    id("io.papermc.paperweight.userdev")
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
    shadowJar {
        dependencies {
            include(dependency("xyz.jpenilla:reflection-remapper"))
        }
    }
    build {
        dependsOn(shadowJar)
    }
    assemble {
        dependsOn(reobfJar)
    }
}

pitest {
    mutationThreshold.set(1)
    coverageThreshold.set(1)
}

kover {
    verify {
        rule {
            bound {
                minValue = 2
                counter = CounterType.INSTRUCTION
                valueType = VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
}

bukkit {
    prefix = "kotlin"
    description = "Kotlin dependency plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.kotlin.KotlinPlugin"
}
