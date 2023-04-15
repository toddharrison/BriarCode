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
    implementation("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.0.0")
}

tasks {
    compileJava {
        options.release.set(17)
    }
    test {
        useJUnitPlatform()
    }
}

pitest {
    verbose.set(false)
    junit5PluginVersion.set("1.1.0")
//    mutators.set(setOf("STRONGER"))
//    avoidCallsTo.set(setOf("kotlin.jvm.internal"))
    threads.set(Runtime.getRuntime().availableProcessors())
}
