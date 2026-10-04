# Shadow Mario (Java)

A 2D side-scrolling platformer in Java, built with the Bagel game library (LWJGL). The player runs through three levels, collects coins and power-ups, avoids enemies, and defeats a fireball-shooting boss to reach the end flag.

## Features
- Three levels loaded from CSV files, selected from a start screen
- Player movement with gravity and jumping, scrolling world
- Enemies, a boss that trades fireballs with the player, and health/score tracking
- Power-ups: double score and invincibility
- Static and flying platforms
- Start, win and game-over screens driven by a simple state machine

## Design
Object-oriented design with inheritance and interfaces:
- `Entity` base class for all game objects
- `Pickup` subclasses: `Coin`, `DoubleScorePower`, `InvinciblePower`
- `Damageable` interface for anything that takes damage (`Player`, `EnemyBoss`)
- `Level` manages entities and game logic; `ShadowMario` switches between screens

## Controls
| Key | Action |
|---|---|
| `1` / `2` / `3` | Choose level |
| `←` / `→` | Move |
| `↑` | Jump |
| `S` | Shoot fireball (boss level) |
| `Space` | Continue after win/lose |
| `Esc` | Quit |

## Run
Requires Java and Maven.
```bash
mvn compile exec:java -Dexec.mainClass=ShadowMario
```
Or open the project in IntelliJ IDEA and run `ShadowMario.java`.

## Credits
- Project framework and assets provided by SWEN20003 Object Oriented Software Development, University of Melbourne. Asset credits in `res/credit.txt`.
- Built with the Bagel library (University of Melbourne).
