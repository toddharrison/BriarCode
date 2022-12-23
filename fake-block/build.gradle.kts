import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

version = "1.0.0-SNAPSHOT"
description = ""

val apiVersion = "1.0"

dependencies {
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    testImplementation("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")

    // Logging Filters
    implementation("org.apache.logging.log4j", "log4j-core", "2.17.2")

    // CommandAPI
    implementation("com.comphenix.protocol", "ProtocolLib", "5.0.0-SNAPSHOT")

    // WorldEdit
    implementation("com.sk89q.worldedit", "worldedit-bukkit", "7.2.10")

    // Test Mock
    testImplementation("org.mockito", "mockito-junit-jupiter", "4.6.1")
}

task("apiJar", type = Jar::class) {
    archiveFileName.set("fake-block-api-${apiVersion}.jar")
    from(sourceSets.main.get().output) {
        include("com/briarcraft/fakeblock/api/**")
    }
}

tasks.named("assemble") {
    dependsOn("apiJar")
}

// Configure plugin.yml generation
// https://github.com/Minecrell/plugin-yml
bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "$group.fakeblock.FakeBlockPlugin"
    apiVersion = "1.19"
    prefix = project.name
    authors = listOf("toddharrison")
    depend = listOf("WorldEdit", "ProtocolLib")
    commands {
        register("fakeblock") {
            description = "Execute fakeblock commands"
            aliases = listOf("fb")
            permission = "fakeblock"
            permissionMessage = "You may not use fakeblock commands!"
        }
    }
    permissions {
        register("fakeblock") {
            description = "Allows you to run the fakeblock commands"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
