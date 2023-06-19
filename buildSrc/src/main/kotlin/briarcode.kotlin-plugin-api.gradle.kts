//import org.gradle.kotlin.dsl.dependencies
import org.gradle.accessors.dm.LibrariesForLibs

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

plugins {
    id("briarcode.kotlin-common")
    id("briarcode.paper")
    id("briarcode.publish")
}

group = "com.briarcraft"

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
}
