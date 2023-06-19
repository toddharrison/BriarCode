import org.gradle.accessors.dm.LibrariesForLibs

// https://github.com/gradle/gradle/issues/15383
val libs = the<LibrariesForLibs>()

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
        minecraftVersion(libs.versions.minecraft.get())
    }
}

bukkit {
    main = "com.briarcraft"
    apiVersion = libs.versions.bukkit.api.get()
    prefix = project.name
    authors = listOf("toddharrison")
}
