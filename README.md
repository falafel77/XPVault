# 🛡️ XPVault Plugin – Comprehensive Technical Guide

## What is XPVault?
XPVault is an advanced plugin for Minecraft that allows players to securely manage their experience points (XP).
It introduces a personal XP vault system where players can save, retrieve, and transfer XP, while also giving server administrators full control over XP distribution through administrative commands.
XPVault enhances multiplayer gameplay by offering reliability and customization.

---

## 🆕 New Features in v1.7
- **World-specific XP vaults:** Configure which worlds allow XP saving and retrieval.
- **Per-command cooldowns:** Set cooldowns for each command, or disable cooldowns from config.yml.
- **More PlaceholderAPI variables:** Supports `%xpvault_saved_xp%`, `%xpvault_savedlevels%`, `%xpvault_currentxp%`, `%xpvault_currentlevels%`.
- **Update notifications:** Operators are notified in-game when a new version is available.
- **Cleaner codebase:** Unused code and comments removed.
- **English-only messages and documentation.**
- **Direct Modrinth link for updates:** All new versions are always available at [Modrinth](https://modrinth.com/plugin/xpvault)

---

## ✅ Key Features
- Secure XP vault system to store XP
- Recover saved XP anytime — especially after death
- Send and receive XP between players
- Full administrative control over XP balances
- Full message customization via `messages.yml`
- Built-in support for PlaceholderAPI integration

---

## 🧱 Project Structure
- `pom.xml` – Maven configuration and dependencies
- `src/main/java/...` – Source code including command handlers, XP logic, and API integrations
- `plugin.yml` – Plugin metadata and command registration
- `messages.yml` – Customizable in-game messages for players and admins

---

## 🧭 Available Commands

| Command | Function | Alias | Permission | Cooldown | Example |
|---------|----------|-------|------------|----------|---------|
| /savexp <amount> | Save XP to your personal vault | /sxp | Open to all | Configurable | /savexp 100 |
| /givexp <player> <amount> | Give XP to another player | /gxp | Open to all | Configurable | /givexp Steve 500 |
| /retrievexp [amount] | Retrieve XP from your vault | /rxp | Open to all | Configurable | /retrievexp or /retrievexp 200 |
| /adminxp <resetall\|set\|add\|remove> [player] [amount] | Admin XP controls | /axp | xpvault.admin | - | /adminxp set Player1 1000 |
| /checkxp | Display current and stored XP and levels | /cxp | Open to all | - | - |
| /xpvaultreload | Reloads the plugin configuration | - | xpvault.reload | - | - |

---

## 🔗 PlaceholderAPI Integration

**Supported Placeholders:**
| Placeholder | Description |
|-------------|-------------|
| %xpvault_saved_xp% | Amount of XP saved in the player's vault |
| %xpvault_savedlevels% | Number of levels saved in the vault |
| %xpvault_currentxp% | Player's current XP |
| %xpvault_currentlevels% | Player's current XP level |

**Example Usage (Scoreboard):**
```yml
lines:
  - "&aStored XP: &f%xpvault_saved_xp%"
  - "&aStored Levels: &f%xpvault_savedlevels%"
  - "&aCurrent XP: &f%xpvault_currentxp%"
  - "&aCurrent Levels: &f%xpvault_currentlevels%"
```

---

## 💬 Message Customization

**Supported Placeholders:**
| Placeholder | Description |
|-------------|-------------|
| %player% | Target player name |
| %amount% | XP amount |
| %current_xp% | Player's current XP |
| %saved_xp% | Player's saved XP |
| %sender% | Player who sent the XP |
| %action% | Action taken (set, add, remove) |
| %new_xp% | New XP value after the action |

**Example Messages (`messages.yml`):**
- `plugin_enabled`: "&aXPVault has been enabled!"
- `xp_saved`: "&aYour XP has been saved!"
- `xp_given`: "&aSuccessfully gave %amount% XP to %player%!"
- `xp_retrieved`: "&aYou retrieved %amount% XP from your vault."
- `adminxp_player_success`: "&a%action% %amount% XP for %player%. New saved XP: %new_xp%"

