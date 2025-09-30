# XPVault Plugin – Commands, Aliases, and PlaceholderAPI Usage

## 📋 Commands & Aliases

### 1. /savexp <amount>
- **Function:** Save XP to your personal vault.
- **Alias:** /sxp
- **Permission:** Open to all players
- **Cooldown:** Configurable per world and per command
- **Example:** `/savexp 100`

### 2. /givexp <player> <amount>
- **Function:** Give XP to another player.
- **Alias:** /gxp
- **Permission:** Open to all players
- **Cooldown:** Configurable per world and per command
- **Example:** `/givexp Steve 500`

### 3. /retrievexp [amount]
- **Function:** Retrieve XP from your vault.
- **Alias:** /rxp
- **Permission:** Open to all players
- **Cooldown:** Configurable per world and per command
- **Example:** `/retrievexp` or `/retrievexp 200`

### 4. /adminxp <resetall|set|add|remove> [player] [amount]
- **Function:** Administrative XP controls (reset, set, add, remove).
- **Alias:** /axp
- **Permission:** `xpvault.admin` required
- **Example:** `/adminxp set Player1 1000`

### 5. /checkxp
- **Function:** Display current and stored XP and levels.
- **Alias:** /cxp
- **Permission:** Open to all players

### 6. /xpvaultreload
- **Function:** Reloads the XPVault plugin configuration.
- **Permission:** `xpvault.reload` required

---

## 🏷️ PlaceholderAPI Variables & Usage

**Supported Placeholders:**
- `%xpvault_saved_xp%`: Amount of XP saved in the player's vault
- `%xpvault_savedlevels%`: Number of levels saved in the vault
- `%xpvault_currentxp%`: Player's current XP
- `%xpvault_currentlevels%`: Player's current XP level

**Example Usage (Scoreboard):**
```yml
lines:
  - "&aStored XP: &f%xpvault_saved_xp%"
  - "&aStored Levels: &f%xpvault_savedlevels%"
  - "&aCurrent XP: &f%xpvault_currentxp%"
  - "&aCurrent Levels: &f%xpvault_currentlevels%"
```

---

## 🧩 plugin.yml Analysis & Detailed Explanation

### Main Sections:
- **name:** Plugin name (XPVault)
- **version:** Current version (e.g., 1.7)
- **main:** Main class path (`com.falafel77.XPVault.XPVault`)
- **api-version:** Minimum supported PaperMC version (e.g., 1.13)
- **libraries:** External libraries required (e.g., sqlite-jdbc)
- **website:** Project/updates link (Modrinth, GitHub)

### Commands Block:
- **savexp:**
  - Description, usage, aliases
- **givexp:**
  - Description, usage, aliases
- **retrievexp:**
  - Description, usage, aliases
- **adminxp:**
  - Description, usage, permission, aliases
- **checkxp:**
  - Description, usage, aliases
- **xpvaultreload:**
  - Description, usage, permission

### Permissions:
- `xpvault.admin`: Allows access to admin commands
- `xpvault.reload`: Allows reloading the plugin configuration

### How plugin.yml Works:
- Registers all commands and their aliases for the server
- Defines permissions for admin/reload commands
- Specifies required libraries for automatic loading
- Sets plugin metadata for server and plugin managers

---

## 💡 Additional Notes
- All commands and placeholders are configurable via config.yml and messages.yml
- Cooldowns and world restrictions can be set for each command
- PlaceholderAPI integration allows dynamic XP display in scoreboards, chat, and other plugins
- For more details, visit: https://modrinth.com/plugin/xpvault

---

**Author:** falafel77
**Year:** 2025
