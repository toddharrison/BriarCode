plugins {
    id("briarcode.kotlin-common")
    id("briarcode.plugin")

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

pitest {
    avoidCallsTo.set(setOf("kotlin.jvm.internal"))
}
