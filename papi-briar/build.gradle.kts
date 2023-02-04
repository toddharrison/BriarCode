plugins {
    id("briarcode.java-common")
    id("briarcode.paper")
}

version = "1.0.1"
description = ""

dependencies {
    implementation("me.clip", "placeholderapi", "2.10.9") {
        exclude("org.bstats", "bstats-bukkit")
    }
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = BigDecimal(0.0)
                }
            }
        }
    }
}

pitest {
    mutationThreshold.set(0)
    coverageThreshold.set(0)
}
