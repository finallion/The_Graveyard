# The Graveyard

Adds new bosses, structures, mobs and blocks themed around the graveyard.

## Downloads

- Fabric and NeoForge: [Modrinth](https://modrinth.com/mod/the-graveyard-fabric), [CurseForge](https://www.curseforge.com/minecraft/mc-mods/the-graveyard-fabric)
- Forge (1.20.1 only): [Modrinth](https://modrinth.com/mod/the-graveyard-forge), [CurseForge](https://www.curseforge.com/minecraft/mc-mods/the-graveyard-forge)

## Versions and loaders

| Minecraft | Loader | Required dependencies |
| --- | --- | --- |
| 1.21.1 | Fabric (Loader 0.16.0 or newer) | Fabric API 0.102.0 or newer, GeckoLib 4 (4.9.3 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |
| 1.21.1 | NeoForge 21.1 | GeckoLib 4 (4.9.3 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |
| 1.21.8 | Fabric (Loader 0.16.0 or newer) | Fabric API 0.129.0 or newer, GeckoLib 5 (5.2.0 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |
| 1.21.8 | NeoForge 21.8 | GeckoLib 5 (5.2.0 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |
| 1.21.11 | Fabric (Loader 0.19.5 or newer) | Fabric API 0.141.6 or newer, GeckoLib 5 (5.4.5 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |
| 1.21.11 | NeoForge 21.11 | GeckoLib 5 (5.4.5 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |
| 26.1.2 | Fabric (Loader 0.19.5 or newer) | Fabric API 0.155.3 or newer, GeckoLib 5 (5.5.2 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |
| 26.1.2 | NeoForge 26.1 | GeckoLib 5 (5.5.2 or newer), VillagersTradingPlus 1.x (1.1.0 or newer) |

The current version is 4.0.0. 1.20.1 needs Java 17. 1.21.1, 1.21.8 and 1.21.11 need Java 21. 26.1, 26.2 and 26.3 need Java 25.

## What's in it

Boss:

- Corrupted Champion

Mobs:

- Acolyte, Ghoul, Ghouling, Revenant, Wraith, Nightmare, Reaper (off by default, can be turned on in the config)
- Skeleton Creeper, Falling Corpse, Tormented Villager
- Corrupted Vindicator, Corrupted Pillager, Corrupted Evoker
- Raven, Crow, Magpie
- Nameless Hanged, a trader

Structures:

- Graveyards: small, medium and large, plus small grave, small desert grave, small desert graveyard, small mountain grave and small savanna grave
- Crypt, Haunted House, the Corrupted Champion's prison, Altar, Ruins
- Dead Tree, Memorial Tree, Giant Mushroom, Mushroom Grave
- Overrun villages in five variants: plains, desert, savanna, snowy and taiga

Blocks:

- Skeleton and bone decoration blocks, carved in the Ossuary from bone blocks
- Urns, including large terracotta urns in 17 variants
- Lore Book blocks, and a Chronicle with 36 lore pages. The Chronicle opens from a button in the inventory.

## Summoning the Corrupted Champion

- Fill a Vial of Blood: kill villagers with a Bone Dagger while you carry the vial. Eight kills fill it.
- Lay the three Ominous Bone Staff Fragments in a row seven blocks in front of the altar: the middle piece straight in line with the altar, the two ends three blocks to each side of it.
- At night, and not on Peaceful, use the full vial on the altar. The vial empties and the Champion rises.

## Configuration

The config file is `graveyard.json5` in the `graveyard` folder of the loader's config directory. It has switches for each structure, most mobs and each overrun village variant, plus spawn weights, hordes, fog and the Champion's stats. The option `structuresOnly` stops all natural mob spawns, spawners and hordes, while spawn eggs and commands still work. On NeoForge, spawn weights and group sizes are set in the `.json` files under `data/graveyard/neoforge/biome_modifier`.

## Building from source

```
gradlew build
```

The jars are written to `fabric/build/libs` and `neoforge/build/libs`.

## Issues

Report bugs and suggestions at https://github.com/finallion/The-Graveyard-FABRIC/issues

## License

GNU GPL v3. See [LICENSE](LICENSE).
