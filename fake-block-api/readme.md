# BriarCode Fake Block API
This Java API is used by plugins that wish to integrate with fake-block services and capabilities.

Currently tested on:
* Paper 19.2

***NOTE: Still in beta! Test before using on a production server.***

## Table of Contents
* [Usage](#usage)
* [Services](#services)
* [Events](#events)
* [Suggestions and Support](#suggestions-and-support)

## Usage

***FUTURE: The repo is not yet configured. Download and include manually for now.***

First, add the repo to your build:

***TBD***

Next, include this library in your plugin using either the Gradle coordinate (kotlin):
```kotlin
implementation("com.briarcraft:fake-block-api:1.0")
```
(groovy):
```groovy
implementation 'com.briarcraft:fake-block-api:1.0'
```
or the Maven coordinate:
```xml
<dependency>
  <groupId>com.briarcraft</groupId>
  <artifactId>fake-block-api</artifactId>
  <version>1.0</version>
</dependency>
```

Do not shadow the API into your plugin. The release of fake-block has the API included itself already and will be
available at runtime.

## Services
Fake-block publishes several Bukkit services, detailed below.

### GroupService
Get an instance to this service using the following code:
```java
getServer().getServicesManager().getRegistration(GroupService.class);
```
This service provides methods to get, create, update, and delete Groups programmatically. Refer to the
[JavaDoc](src/main/java/com/briarcraft/fakeblock/api/service/GroupService.java) for specific method capabilities.

### PlayerGroupService
Get an instance to this service using the following code:
```java
getServer().getServicesManager().getRegistration(PlayerGroupService.class);
```
This service provides methods to show, hide, and clear Groups for Players. Refer to the
[JavaDoc](src/main/java/com/briarcraft/fakeblock/api/service/PlayerGroupService.java) for specific method capabilities.

## Events
Fake-block emits cancellable Bukkit Events every time it is going to take an action. These events are:

### `CreateFakeBlockGroupEvent`
This event is called when fake-block is about to create a new Group.

### `DeleteFakeBlockGroupEvent`
This event is called when fake-block is about to delete an existing Group.

### `UpdateFakeBlockGroupEvent`
This event is called when fake-block is about to modify the Set of FakeBlocks belonging to an existing Group.

### `ShowFakeBlockGroupEvent`
This event is called when fake-block is about to show a Group to a Player.

### `HideFakeBlockGroupEvent`
This event is called when fake-block is about to hide a Group from a Player.

### `ClearFakeBlockGroupEvent`
This event is called when fake-block is about to clear the Group configurations of a Player.

## Suggestions and Support
Please feel free to provide suggestions (this plugin is in active development) or ask questions using the [BriarCraft
discord plugin](https://discord.gg/ycwxwQXN74) channel.
