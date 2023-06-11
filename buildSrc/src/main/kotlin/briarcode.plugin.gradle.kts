plugins {
    id("briarcode.paper")
    id("briarcode.publish")
    id("net.minecrell.plugin-yml.bukkit")
    id("xyz.jpenilla.run-paper")
}

group = "com.briarcraft"

dependencies {
}

tasks {
    runServer {
        minecraftVersion("1.20")
    }
}

bukkit {
    main = "com.briarcraft"
    apiVersion = "1.20"
    prefix = project.name
    authors = listOf("toddharrison")
}
