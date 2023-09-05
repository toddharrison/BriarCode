plugins {
    id("briarcode.kotlin-plugin-api")
}

version = "2.1-SNAPSHOT"
description = ""

dependencies {
    api(project(":kotlin"))
    api(project(":gui-api"))
}
