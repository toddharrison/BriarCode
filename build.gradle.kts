plugins {
    kotlin("jvm") version "1.7.21"
    id("io.papermc.paperweight.userdev") version "1.3.5" apply false
    id("xyz.jpenilla.run-paper") version "1.0.6" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" apply false // Generates plugin.yml
    id("info.solidsoft.pitest") version "1.9.11" apply true // Pitest
    id("maven-publish") apply false // Maven publish
}

repositories {
    gradlePluginPortal()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "xyz.jpenilla.run-paper")
    apply(plugin = "net.minecrell.plugin-yml.bukkit")
    apply(plugin = "info.solidsoft.pitest")
    apply(plugin = "maven-publish")

    group = "com.briarcraft"

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
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
        maven("https://repo.jpenilla.xyz/snapshots/") // reflection-remapper
        maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    }

    dependencies {
        api(platform("org.jetbrains.kotlin:kotlin-bom"))
        api("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")

        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

        implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.4.0")
        implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.4.0")

        // Test
        testImplementation(kotlin("test"))
        testImplementation("org.mockito.kotlin", "mockito-kotlin", "4.0.0")
        testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.0.0")
    }

    tasks {
        compileJava {
            options.release.set(17)
        }
        test {
            useJUnitPlatform()
        }
        runServer {
            minecraftVersion("1.19.2")
        }
    }

    pitest {
        verbose.set(false)
        threads.set(8)
        junit5PluginVersion.set("1.1.0")
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/toddharrison/BriarCode")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }
}
