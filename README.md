# Twerk Grow

Sneak to bonemeal nearby crops, saplings and sugar cane. A Fabric mod for **Minecraft 26.3**
(Java 25, Mojang names). Server-side logic, so it works in singleplayer and on servers.

## Behavior

- Each time you **start sneaking**, growable blocks within 5 blocks (3 vertically) get a bonemeal
  effect. Holding sneak does not repeat; twerk to keep growing.
- Works on crops, saplings, stems, sweet berries, cocoa and similar. Sugar cane grows one block
  taller, up to the vanilla height of 3.
- Grass, ferns, moss, nylium, glow lichen and flowers are left alone.

## Development (PowerShell)

| Task | Command |
|---|---|
| Build + unit tests | `.\gradlew.bat build` |
| Launch the dev client (Mod Menu included) | `.\gradlew.bat runClient` |
| In-game test (plants crops, sneaks, asserts growth) | `.\gradlew.bat runClientGameTest` |
| Everything | `.\scripts\verify.ps1` |

Requires Java 25.

## Credits

Idea and behavior reference: [Sneak Fertilizing](https://github.com/TheAnkulai/sneak-fertilizer-mod) (MIT).
This is a from-scratch rewrite using Mojang names for 26.3.

## License

CC0-1.0.
