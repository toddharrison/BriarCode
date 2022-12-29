import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    kotlin("jvm") version "1.7.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2" // shadowJar
    id("io.papermc.paperweight.userdev") version "1.3.5" // paperweight
}

version = "1.7.21.1"
description = ""

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val paperVersion: String by rootProject.extra

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    api(platform("org.jetbrains.kotlin:kotlin-bom"))
    paperDevBundle(paperVersion)

    library("org.jetbrains.kotlin:kotlin-stdlib")
    library("org.jetbrains.kotlin:kotlin-reflect")
    library("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    library("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    library("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

    library("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.8.0")
    library("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.8.0")

    implementation("xyz.jpenilla:reflection-remapper:0.1.0-SNAPSHOT")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin", "mockito-kotlin", "4.0.0")
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
    test {
        useJUnitPlatform()
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

configure<PublishingExtension> {
    publications {
        register<MavenPublication>("plugin") {
//            from(components["kotlin"])
            artifact(tasks.reobfJar)
            includeDependencies(pom)
        }
    }
}



fun includeDependencies(pom: MavenPom) {
    pom.withXml {
        val dependencies = asNode().appendNode("dependencies")
        configurations.implementation.get().allDependencies
            .filter { it.name != "reflection-remapper" }
            .filter { it.name != "kotlin-bom" }
            .forEach {
                val depNode = dependencies.appendNode("dependency")
                depNode.appendNode("groupId", it.group)
                depNode.appendNode("artifactId", it.name)
                depNode.appendNode("version", it.version)
                depNode.appendNode("scope", "runtime")
            }
        configurations.library.get().allDependencies
            .forEach {
                val depNode = dependencies.appendNode("dependency")
                depNode.appendNode("groupId", it.group)
                depNode.appendNode("artifactId", it.name)
                depNode.appendNode("version", it.version)
                depNode.appendNode("scope", "runtime")
            }
    }
}
