plugins {
    java
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    testImplementation("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")

    testImplementation("org.mockito", "mockito-junit-jupiter", "4.6.1")
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
