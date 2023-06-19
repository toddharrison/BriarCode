import org.gradle.accessors.dm.LibrariesForLibs

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

plugins {
    java
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testImplementation(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testImplementation(libs.mockito.java)
}

tasks {
    test {
        finalizedBy(tasks.jacocoTestReport)
    }
    jacocoTestReport {
        dependsOn(tasks.test)
        finalizedBy(tasks.jacocoTestCoverageVerification)
    }
    jacocoTestCoverageVerification {
        dependsOn(tasks.jacocoTestReport)
    }
}

jacoco {
}
