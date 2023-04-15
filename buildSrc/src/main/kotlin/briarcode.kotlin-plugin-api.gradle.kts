import org.gradle.kotlin.dsl.dependencies

plugins {
    id("briarcode.kotlin-common")
    id("briarcode.paper")
    id("briarcode.publish")
}

group = "com.briarcraft"

kotlin {
    jvmToolchain(19)
}

dependencies {
}
