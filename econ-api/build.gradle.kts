plugins {
    id("briarcode.kotlin-plugin-api")
}

version = "1.3"
description = ""

dependencies {
    api(project(":kotlin"))
    api(project(":gui-api"))
}
