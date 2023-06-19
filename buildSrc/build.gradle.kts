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
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}") // Kotlin
    implementation("org.jetbrains.kotlinx:kover:${libs.versions.kover.get()}") // Kover
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:${libs.versions.gradlepitestplugin.get()}") // Pitest
    implementation("net.minecrell:plugin-yml:${libs.versions.pluginyml.get()}") // Generates plugin.xml during build
    implementation("xyz.jpenilla:run-task:${libs.versions.runtask.get()}") // Adds runServer and runMojangMappedServer tasks for testing
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:${libs.versions.paperweightuserdev.get()}") // paperweight
}
