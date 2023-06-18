import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import kotlinx.kover.api.CounterType
import kotlinx.kover.api.VerificationValueType

plugins {
    id("briarcode.kotlin-plugin")
    alias(libs.plugins.shadow)
//    alias(libs.plugins.paper.userdev)
    id("io.papermc.paperweight.userdev")
}

version = "1.0.0-${libs.versions.kotlin.get()}"
description = ""

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.get())

    implementation(libs.reflectionremapper)

    library(libs.kotlin.stdlib)
    library(libs.kotlin.reflect)
    library(libs.kotlin.serialization)
    library(libs.kotlin.coroutines.core)
    library(libs.kotlin.coroutines.jdk8)

//    library(libs.kotlin.logging)

    library(libs.mccoroutine.api)
    library(libs.mccoroutine.core)
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
