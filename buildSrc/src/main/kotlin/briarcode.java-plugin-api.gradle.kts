import org.gradle.accessors.dm.LibrariesForLibs

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

plugins {
    id("briarcode.java-common")
    id("briarcode.paper")
    id("briarcode.publish")
}

group = "com.briarcraft"

dependencies {
}
