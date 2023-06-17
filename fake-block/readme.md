# BriarCode Fake Block Plugin
This Spigot plugin allows the creation of groups of client-only blocks that can be shown or hidden to specific players.
They prevent players from walking through them or breaking them.

Currently tested with (will likely work on most combos):
| **Server** | Versions | Dependencies | |
|  |  | ProtocolLib | WorldEdit |
| Paper | 1.20.1 | 654 | 7.2.15-SNAPSHOT |
| Paper | 1.19.3 | 608 | 7-2.13 |
| Paper | 1.18.2 | 600 | 7.2.12 |
| Paper | 1.17.1 | 608 | 7-2.14 |
| | | | |
| Purpur | 1.19.4-1979 | 5.0.0 | FastAsyncWorldEdit-Bukkit-2.6.2-SNAPSHOT-439 |
| Purpur | 1.19.3-1933 | 5.0.0 | FastAsyncWorldEdit-Bukkit-2.6.2-SNAPSHOT-439 |
| Also tested with: | | | |
| | MyWorlds-1.19.3-v3-213 | | |
| | BKCommonLib-1.19.3-v3-1512 | | |

## Table of Contents
* [Installation](#installation)
* [Getting Started](#getting-started)
* [Commands](#commands)
* [Permissions](#permissions)
* [Suggestions and Support](#suggestions-and-support)
* [Future Features](#future-features)

## Installation
To install fake-block:
1. Download and install ProtocolLib and WorldEdit
2. Download and install the latest release of fake-block
    * No plugin configuration is presently required
3. Fake blocks are saved in two yaml files: groups.yml and playerGroups.yml
    * The groups yaml specifies the defined fake-block groups
    * The player groups yaml specifies which player can see which groups

## Getting Started
To create your first fake-block group:
1. Use WorldEdit to make a selection (`//wand` can be used)
2. Run the command `/fakeblock create <group-name>` (or `/fb`)
3. Fake-block will remove the blocks currently there (these become the fake-blocks) and replace them with air (these are the real blocks)

To show the fake-block group to a player:
1. Run the command `/fakeblock show <group-name> <player-name>`
2. The fake blocks will appear to that player only

To hide the fake-block group from a player:
1. Run the command `/fakeblock hide <group-name> <player-name>`
2. The fake blocks will disappear from that player only

To delete the fake-block group:
1. Run the command `/fakeblock delete <group-name>`
2. Fake-block will restore the blocks that had been converted to air

Existing air blocks are ignored when the groups are created. Only real blocks are used for groups.

Blocks in groups may overlap. For example, you could have a group of stone bricks and cracked stone bricks in the same
location and toggle between them. Bear in mind, though, that behavior for when both groups are shown to the same player
at the same time are indeterminate. It might show a mix of the two or behave strangely.

You can also modify an existing group by adding and removing blocks. See the commands below.

## Commands
All of these commands are available as an OP in Minecraft.

### `/fb create <group-name> <is-group-shown-by-default>`
Creates a new group with the specified name using the current WorldEdit selection. `is-group-shown-by-default` is an
optional parameter and defaults to `false`.

### `/fb delete <group-name>`
Deletes the group with the specified name and restores the blocks that were turned to air.
May also be used in the console.

### `/fb add <group-name>`
Adds the current WorldEdit selection to the group with the specified name.

### `/fb remove <group-name>`
Removes the current WorldEdit selection from the group with the specified name.

### `/fb show <group-name> <player-name>`
Shows the specified group to the specified player (player may be online or offline). This shows the fake blocks.
May also be used in the console.

### `/fb hide <group-name> <player-name>`
Hides the specified group from the specified player (player may be online or offline). This shows the real air blocks.
May also be used in the console.

### `/fb setdefault <player-name>`
Clears any show or hide configuration for the specified player, allowing the default to apply.
May also be used in the console.

## Permissions
Fake-block requires only one permission, which allows calling the admin commands above (OP is always allowed):
`fakeblock`

## Suggestions and Support
Please feel free to provide suggestions (this plugin is in active development) or ask questions using the [BriarCraft
discord plugin](https://discord.gg/ycwxwQXN74) channel.

## Future Features
This is a list of features I'm currently considering, checked I'm working on:
* [X] Prevention of players being kicked for flying when walking on fake blocks
* [ ] Group priorities to handle overlapping groups in a deterministic way
* [X] Permission support for showing and hiding groups to players (LuckPerms)
