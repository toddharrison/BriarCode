plugins {
    id("briarcode.kotlin-common")
    id("briarcode.plugin")

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.9.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.9.0")
}

pitest {
    avoidCallsTo.set(setOf("kotlin.jvm.internal"))
}
