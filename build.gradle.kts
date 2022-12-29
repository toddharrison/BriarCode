plugins {
//    kotlin("jvm") version "1.7.21"
    java
    id("io.papermc.paperweight.userdev") version "1.3.5" apply false
    id("xyz.jpenilla.run-paper") version "1.0.6" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2" apply false // Generates plugin.yml
    id("info.solidsoft.pitest") version "1.9.11" apply true // Pitest
}

repositories {
    gradlePluginPortal()
}

val paperVersion by extra { "1.19.2-R0.1-SNAPSHOT" }

subprojects {
//    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "xyz.jpenilla.run-paper")
    apply(plugin = "net.minecrell.plugin-yml.bukkit")
    apply(plugin = "info.solidsoft.pitest")
    apply(plugin = "maven-publish")

    group = "com.briarcraft"

//    kotlin {
//        jvmToolchain {
//            languageVersion.set(JavaLanguageVersion.of(17))
//        }
//    }

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
//        api("io.papermc.paper:paper-api:$paperVersion")
        implementation("io.papermc.paper:paper-api:$paperVersion")

        testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.0.0")
    }

    tasks {
        compileJava {
            options.release.set(17)
        }
        runServer {
            minecraftVersion(paperVersion.split("-")[0])
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
                url = uri("$buildDir/local-repository")
            }
//            maven {
//                name = "GitHubPackages"
//                url = uri("https://maven.pkg.github.com/toddharrison/BriarCode")
//                credentials {
//                    username = System.getenv("GITHUB_ACTOR")
//                    password = System.getenv("GITHUB_TOKEN")
//                }
//            }
        }
    }
}
