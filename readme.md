# BriarCode Repo
This repository serves as the root for all the public resources for the BriarCraft SMP server. Each submodule has its
own readme containing more information and usage about that particular plugin or API. Below are some quick links for
convenience.

## Releases
Download the latest plugin and library releases below, or use the [releases](../../releases) link at right to download
specific versions. If there isn't a release of a module, it's still in beta!
* [kotlin-1.7.21.1](../../releases/download/kotlin-1.7.21.1/kotlin-1.7.21.1.jar)
* [fake-block-2.0.3](../../releases/download/fake-block-2.0.1/fake-block-2.0.3.jar)
* [fake-block-api-2.0](../../releases/download/fake-block-2.0.0/fake-block-api-2.0.jar)

## Plugin APIs
BriarCode offers both Java and Kotlin APIs that may be used to integrate the functionality provided by BriarCode plugins
into independent plugins.

### Java APIs
* [fake-block-api](fake-block-api):
  API for integrating with [fake-block](fake-block)

### Kotlin APIs
* [adventure-api](adventure-api):
  API for integrating with [adventure](adventure)
* [econ-api](econ-api):
  API for integrating with [econ](econ)

## Plugins
BriarCode plugins are implemented in Java and Kotlin. The Kotlin plugins all require the `kotlin` plugin itself, which
is optimized to load the Kotlin dependencies as libraries instead of being shadowed into the jar.

### Java Plugins
* [fake-block](fake-block): Client-side only blocks

### Kotlin Plugins
* [kotlin](kotlin):
  Base plugin for Kotlin language support, also includes some useful utilities and paperweight
* [adventure](adventure): 
  Plugin providing custom items, potion effect enchantments, machines, and loot management
* [command-alias](command-alias):
  Custom command wrappers including temporary roles (admin, creator, questDev, manager)
* [data-source](data-source):
  Reusable DataSource connection service running in asynchronous database thread
* [econ](econ):
  Complex economy model including many variants like dynamically priced recipe-drive market, currencies, etc.
* [gui](gui):
  GUI interface using player inventories with logical panel management without completing achievements
* [region-difficulty](region-difficulty):
  Nerf mobs based on WorldGuard regions to simulate all difficulty levels in one world
* [region-scan](region-scan):
  Pre-scan a world to generate all the chunks for performance, in a spiral pattern
* [return-to-wild](return-to-wild):
  Restore your world to a pristine state, reverting player and mob changes over time

## Extensions

### Java
* [papi-briar](papi-briar): PAPI extension providing some new, interesting properties required by BriarCode plugins

## Latest Builds
Under [actions](../../actions) above you can download snapshot builds of plugins, if available, in the artifacts section
of each build. **Do not use builds on production servers!**

## Developer
Look in [buildSrc](buildSrc) for some of my development notes. Usage of plugin APIs will be included in their own
readmes. Much of this code is provided for reference, and may not be suitable for your server in its current form.
