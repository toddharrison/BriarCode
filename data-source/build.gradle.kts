import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder

plugins {
    id("briarcode.kotlin-plugin")
}

version = "1.2.0"
description = ""

dependencies {
    api(project(":kotlin"))

    library("com.zaxxer", "HikariCP", "5.0.1")
    library("org.slf4j", "slf4j-jdk14", "1.7.36")
    library("mysql", "mysql-connector-java", "8.0.33")
    library("org.mariadb.jdbc", "mariadb-java-client", "3.1.4")
    library("com.h2database", "h2", "2.1.214")

    testImplementation("com.zaxxer", "HikariCP", "5.0.1")
    testImplementation("com.h2database", "h2", "2.1.214")
}

pitest {
    mutationThreshold.set(0)
    coverageThreshold.set(0)
}

kover {
    verify {
        rule {
            bound {
                minValue = 0
                counter = kotlinx.kover.api.CounterType.INSTRUCTION
                valueType = kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
}

bukkit {
    prefix = "data-source"
    description = "data-source plugin"
    load = PluginLoadOrder.STARTUP
    main = "com.briarcraft.datasource.DataSourcePlugin"
    depend = listOf("kotlin")
}
