plugins {
    id("briarcode.kotlin-plugin-api")
}

version = "1.4"
description = ""

dependencies {
    api(project(":kotlin"))
    api(project(":gui-api"))
}
