# Azimuth

Azimuth is a Create addon library that expands Create APIs for addon developers.

## Links

- [Documentation](https://azimuth.azmod.net)

## What It Does

- **Super Block Entity Behaviours** for composable, high-capability behaviour components.
- **Advancements** for Create-style advancement definitions and awarding.
- **Outlines** for additional animated outline helpers, especially useful in Ponder scenes.

## Installation

1. Install NeoForge for Minecraft `1.21.1`.
2. Install Create for `1.21.1`.
3. Place the Azimuth JAR in the `mods` folder.

## Development quick start guide

Add Azimuth to the `build.gradle` dependencies block (replace `<version>` with the version to target):

```groovy
dependencies {
	implementation "com.cake.azimuth:azimuth:<version>"
}
```

Declare the dependency in `neoforge.mods.toml`:

```toml
[[dependencies.yourmodid]]
	modId = "azimuth"
	type = "required"
	versionRange = "[<version>,)"
	ordering = "AFTER"
	side = "BOTH"
```

## What's Included

### Super Block Entity Behaviours

Composable behaviour components for `SmartBlockEntity`, including lifecycle hooks, behaviour lookup helpers, and extension interfaces for kinetics, rendering, and schematic requirements.

### Advancements

Create-style advancement definitions and awarding, backed by `AzimuthAdvancementProvider`, `AzimuthAdvancement`, and `AzimuthAdvancementBehaviour`.

### Outlines

Additional Catnip outliner types for visual guidance, including `ExpandingLineOutline` and `ExpandingLineOutlineInstruction` for Ponder scenes.
