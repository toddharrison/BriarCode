plugins {
    id("briarcode.kotlin-plugin-api")
}

version = "2.1-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.mccoroutine.api)
    implementation(libs.mccoroutine.core)
}
