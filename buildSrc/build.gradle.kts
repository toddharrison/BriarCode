plugins {
    `kotlin-dsl`
//    id("org.gradle.kotlin.kotlin-dsl") version "4.0.1"
//    id("org.gradle.kotlin.kotlin-dsl") version "3.2.7"
//    id("org.gradle.kotlin.kotlin-dsl") version "2.4.1"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") // PaperMC, paperweight
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0") // Kotlin
    implementation("org.jetbrains.kotlinx:kover:0.6.1") // Kover
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.9.11") // Pitest
    implementation("net.minecrell:plugin-yml:0.5.2") // Generates plugin.xml during build
    implementation("xyz.jpenilla:run-task:2.0.1") // Adds runServer and runMojangMappedServer tasks for testing
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.4.0") // paperweight
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2") // shadowJar
}
