# BriarCode BuildSrc
I made the decision to use one repo for all the BriarCraft plugins so that I could take advantage of gradle buildSrc to
provide templates and common configuration across all the plugins. The template features I have provided are:

* briarcode.java-common: Java development
  * jacoco test coverage
* briarcode.kotlin-common: Kotlin development
  * kover test coverage
* briarcode.publish: GitHub Publishing
  * publish to GitHub packages
* briarcode.paper: Paper Plugins
  * target Java 17
  * configures common repositories
  * pitest mutation test coverage
* briarcode.plugin: Base Plugins
  * inherits from `paper` and `publish`
  * run-paper server execution (`./gradlew :plugin:runServer`)
  * auto generation of plugin.yml
* briarcode.java-plugin: Java Plugins
  * inherits from `java-common` and `plugin`
  * creates releasable Java plugins
* briarcode.java-plugin-api: Java Plugin APIs
  * inherits from `java-common`, `paper`, and `publish`
* briarcode.kotlin-plugin: Kotlin Plugins
  * inherits from `kotlin-common` and `plugin`
  * creates releasable Kotlin plugins
