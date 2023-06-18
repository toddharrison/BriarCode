import org.gradle.accessors.dm.LibrariesForLibs

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

plugins {
    java
    id("info.solidsoft.pitest")

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") // PaperMC
    maven("https://repo.jpenilla.xyz/snapshots/") // reflection-remapper
    maven("https://hub.spigotmc.org/nexus/content/repositories/public/") // MCCoroutine
    maven("https://maven.playpro.com/")
    maven("https://maven.enginehub.org/repo/") // WorldGuard, WorldEdit
    maven("https://jitpack.io") // Vault, CommandAPI
    maven("https://repo.codemc.org/repository/maven-public/") // CommandAPI dependencies
//    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PAPI
    maven("https://betonquest.org/nexus/repository/betonquest/") // BetonQuest
}

dependencies {
    implementation(libs.paper)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

tasks {
    compileJava {
        options.release.set(libs.versions.java.get().toInt())
    }
    test {
        useJUnitPlatform()
    }
}

pitest {
    verbose.set(false)
    junit5PluginVersion.set(libs.versions.pitest.junitplugin)
//    mutators.set(setOf("STRONGER"))
//    avoidCallsTo.set(setOf("kotlin.jvm.internal"))
    threads.set(Runtime.getRuntime().availableProcessors())
}
