plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    api(platform("org.jetbrains.kotlin:kotlin-bom"))

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin", "mockito-kotlin", "4.0.0")
}

tasks {
    test {
        finalizedBy(tasks.koverHtmlReport)
    }
    koverHtmlReport {
        dependsOn(tasks.test)
    }
}

kover {
}
