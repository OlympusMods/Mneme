# Mneme

Notes and checklists inside Minecraft, with coordinates and pinned overlays.

Mneme keeps your notes, timers, calculations and macros one key away. Press **N** in game (rebindable under Controls) to open the menu.

## Features

**Notes**
- Text notes and checklists, saved in `config/mneme/notes.json`
- Word wrapped editor with a title, so long notes stay readable
- A **Coords** button that writes your position and dimension where the cursor is, in a note or in a checklist item
- Eight colors and a paper, plain or black material for every note
- Pin notes to the left or right edge of the screen and scale them from 50% to 200%

**Timers**
- Start a timer from a preset or type a time like `10m`, `1h30m`, `90s` or `1:30`
- A toast and a sound when it rings, even for a timer that ran out while the game was closed

**Calculator**
- Typed math with `+ - * / % ^`, brackets, `sqrt`, `min`, `max`, `ans` and more
- Understands Minecraft sizes: `3 stacks + 12`, `2 shulkers / 5`, `1 dchest`
- Shows big results as shulkers, stacks and items, and keeps a history

**Macros**
- Run a script when you press a key, join a world, die, every few seconds, or when a chat message contains some text
- The script is one command per line: `say`, `command`, `echo`, `toast`, `wait`, and `repeat` with `end`
- `{x}`, `{y}`, `{z}`, `{dimension}` and `{player}` work in any line, and `{message}` in chat macros

```
say I am at {x} {y} {z}
wait 2s
repeat 3
  echo still here
  wait 1s
end
command time set day
```

Waits take ticks (`20t`), seconds (`2s`) or just a number of seconds. Lines starting with `#` are ignored.

**Appearance** lives on the Proteus card at the top of the menu and is shared with every Olympus mod through [Proteus](https://github.com/OlympusMods/Proteus): Modern or Classic style, accent color, blur, animations and where toasts appear.

## Supported versions

| Minecraft | Branch | Fabric | Forge | NeoForge |
|-----------|--------|:------:|:-----:|:--------:|
| 1.20-1.20.2 | [`1.20`](https://github.com/OlympusMods/Mneme/tree/1.20) | ✅ | ✅ | n/a |
| 1.20.3-1.20.4 | [`1.20.3`](https://github.com/OlympusMods/Mneme/tree/1.20.3) | ✅ | ✅ | ✅ |
| 1.20.5-1.20.6 | [`1.20.5`](https://github.com/OlympusMods/Mneme/tree/1.20.5) | ✅ | ✅ | ✅ |
| 1.21-1.21.1 | [`1.21`](https://github.com/OlympusMods/Mneme/tree/1.21) | ✅ | ✅ | ✅ |
| 1.21.2-1.21.3 | [`1.21.2`](https://github.com/OlympusMods/Mneme/tree/1.21.2) | ✅ | ✅ (1.21.3) | ✅ (1.21.3) |
| 1.21.4-1.21.5 | [`1.21.4`](https://github.com/OlympusMods/Mneme/tree/1.21.4) | ✅ | ✅ | ✅ |
| 1.21.6-1.21.8 | [`1.21.6`](https://github.com/OlympusMods/Mneme/tree/1.21.6) | ✅ | ✅ | ✅ |
| 1.21.9-1.21.10 | [`1.21.9`](https://github.com/OlympusMods/Mneme/tree/1.21.9) | ✅ | ✅ | ✅ |
| 1.21.11 | [`1.21.11`](https://github.com/OlympusMods/Mneme/tree/1.21.11) | ✅ | ✅ | ✅ |
| 26.1-26.1.2 | [`26.1`](https://github.com/OlympusMods/Mneme/tree/26.1) | ✅ | ✅ | ✅ |
| 26.2-26.3 | [`main`](https://github.com/OlympusMods/Mneme/tree/main) | ✅ | ✅ | ✅ |

Each range of Minecraft versions that one jar per loader can cover has its own branch, named after the first version of the range. `main` is always the newest one.

Fabric needs Fabric API. NeoForge has no stable release for 1.20.2 or 1.21.2 and Forge has none for 1.21.2.


## Installing

1. Download the jar for your loader and Minecraft version from [Modrinth](https://modrinth.com/mod/mneme) or [Releases](https://github.com/OlympusMods/Mneme/releases).
2. Put it in your `mods` folder together with [Proteus](https://github.com/OlympusMods/Proteus), the UI library Mneme draws its menu with. Fabric also needs Fabric API.

## Building

Each branch is one root Gradle build that drives a project per loader: `fabric`, `forge` and `neoforge`. They all compile the shared code in `common`. Every loader project is a Gradle build of its own, so it can also be opened and built on its own. Use the JDK each loader needs (`settings.gradle` lists them; Fabric needs Java 25 to run Gradle itself).

```sh
# one loader
./gradlew :fabric:build

# every loader on this branch
./gradlew build
```

Finished jars are collected into `build/dist` and named `mneme-<loader>-<minecraft versions>-<version>.jar`. The mod version is set in the root `gradle.properties`.

## Publishing

Releases go to Modrinth and CurseForge from the same build. Put `modrinthToken` and `curseforgeToken` in `~/.gradle/gradle.properties` (or set `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN`), set the two project ids at the top of `publish.gradle`, and add a `## <version>` entry to `CHANGELOG.md`, which becomes the changelog of every upload.

```sh
# print what would be uploaded, send nothing
./gradlew publishMods -PdryRun=true

# one site, or both
./gradlew publishModrinth
./gradlew publishCurseforge
./gradlew publishMods

# one loader
./gradlew :neoforge:publishModrinth
```

Without any token the tasks only do a dry run. The Minecraft versions each jar is uploaded for come from `publish_minecraft_versions` in each loader's `gradle.properties`. Modrinth keeps the environment on each version, so `publishModrinth` finishes by setting the new version to client only; change that in `publish.gradle` if the mod also runs on servers.

## Project layout

This branch holds Minecraft 1.21 to 1.21.1. The other ranges are on the branches listed above.

```
common/src/main/java        Shared code
common/src/main/resources   Language file, textures and icon
fabric/  forge/  neoforge/  Thin per-loader projects
publish.gradle              Modrinth and CurseForge setup shared by every loader
tools/sync.sh               Copies a folder from this branch to every other branch
```

`common` holds loader- and version-independent logic in `core` (notes, timers, calculator, macros), the screens in `client/ui`, and the glue for this Minecraft version in `client` and `mixin`. Every branch keeps the same paths, so a fix is made once and cherry-picked to the other branches. A folder that is the same everywhere can also be copied over in one go, for example `tools/sync.sh common/src/main/java/com/github/olympus/mneme/core`, which commits it to every other branch.

When a Minecraft update breaks the code, cut a new branch from the newest one, fix what the compiler reports, and narrow the version range on the branch it came from. A branch covers the range where one jar per loader still loads and its mixin targets still exist.

## Contributing

Bug reports and pull requests are welcome. For a bug, include your Minecraft version, loader and `latest.log`. Commit messages follow `type: short description` (`feat`, `fix`, `chore`, `refactor`).

## License

[GPL-3.0](LICENSE)
