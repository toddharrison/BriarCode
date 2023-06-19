plugins {
    id("briarcode.kotlin-plugin-api")
}

version = "2.0-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))
    api(project(":gui-api"))
}
