import org.gradle.accessors.dm.LibrariesForLibs

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

plugins {
//    alias(libs.plugins.kotlin)
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    api(platform("org.jetbrains.kotlin:kotlin-bom"))

//    implementation(libs.kotlin.logging)

    testImplementation(kotlin("test"))
    testImplementation(libs.mockito.kotlin)
}

tasks {
    test {
        finalizedBy(tasks.koverHtmlReport)
    }
    koverHtmlReport {
        dependsOn(tasks.test)
    }
}

koverReport {
}
