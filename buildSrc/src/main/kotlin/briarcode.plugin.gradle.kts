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
        minecraftVersion("1.19.2")
    }
}

bukkit {
    main = "com.briarcraft"
    apiVersion = "1.19"
    prefix = project.name
    authors = listOf("toddharrison")
}
