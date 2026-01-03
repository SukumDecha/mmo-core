# MMO Core

A multi-profession skill leveling system for Minecraft servers.

---

**Status:** `Completed`

## Tech Stack

- Java 8 + Maven
- Purpur/Spigot API 1.20.2
- ProtocolLib, ItemsAdder, MythicMobs
- Lombok

## Key Engineering Highlights

- **Event-Driven Architecture** with Bukkit listeners for 5 skill categories
- **Singleton & Observer Patterns** for plugin lifecycle and event handling
- **Configuration-Driven Design** with hot-reload support (YAML-based)
- **Async Data Persistence** to prevent server blocking
- **Cross-Skill Dependencies** system for advanced progression gates

## Quick Start

```bash
# Build
mvn clean package

# Deploy
cp target/mmo-core-*.jar /path/to/server/plugins/

# Reload in-game
/madmin reload
```

---

MIT © xfrozenz_736
