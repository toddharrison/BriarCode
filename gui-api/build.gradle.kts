plugins {
    id("briarcode.kotlin-plugin-api")
}

version = "1.0"
description = ""

dependencies {
    api(project(":kotlin"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.9.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.9.0")
}
