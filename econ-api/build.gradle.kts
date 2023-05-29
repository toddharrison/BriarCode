plugins {
    id("briarcode.kotlin-plugin-api")
}

version = "1.5"
description = ""

dependencies {
    api(project(":kotlin"))
    api(project(":gui-api"))
}
