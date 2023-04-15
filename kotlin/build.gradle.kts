import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import kotlinx.kover.api.CounterType
import kotlinx.kover.api.VerificationValueType

plugins {
    id("briarcode.kotlin-plugin")
    id("com.github.johnrengelman.shadow")
    id("io.papermc.paperweight.userdev")
}

version = "1.8.0.3"
description = ""

dependencies {
    paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    implementation("xyz.jpenilla:reflection-remapper:0.1.0-SNAPSHOT")

    library("org.jetbrains.kotlin:kotlin-stdlib")
    library("org.jetbrains.kotlin:kotlin-reflect")
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

//    library("io.github.microutils", "kotlin-logging-jvm", "3.0.5")

    library("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.9.0")
    library("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.9.0")
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
    mutationThreshold.set(0)
    coverageThreshold.set(0)
}

kover {
    verify {
        rule {
            bound {
                minValue = 1
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

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) { skip() }
javaComponent.addVariantsFromConfiguration(configurations["reobf"]) {
    mapToMavenScope("runtime")
    dependencies {
        configurations.library.get().allDependencies
            .forEach {
                add("reobf", "${it.group}:${it.name}:${it.version}")
            }
    }
}
