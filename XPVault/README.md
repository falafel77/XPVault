🛡️ XPVault Plugin – Comprehensive Technical Guide

What is XPVault?

XPVault is an advanced plugin for Minecraft that allows players to securely manage their experience points (XP).
It introduces a personal XP vault system where players can save, retrieve, and transfer XP, while also giving server administrators full control over XP distribution through administrative commands. XPVault enhances multiplayer gameplay by offering reliability and customization.


---

✅ Key Features

Secure XP vault system to store XP.

Recover saved XP anytime — especially after death.

Send and receive XP between players.

Full administrative control over XP balances.

Full message customization via messages.yml.

Built-in support for PlaceholderAPI integration.



---

🧱 Project Structure

pom.xml – Maven configuration and dependencies.

src/main/java/... – Source code including command handlers, XP logic, and API integrations.

plugin.yml – Plugin metadata and command registration.

messages.yml – Customizable in-game messages for players and admins.



---

🧭 Available Commands

/savexp <amount>

Function: Save XP to your personal vault.

Alias: /sxp

Permission: Open to all players

Example: /savexp 100



---

/givexp <player> <amount>

Function: Give XP to another player.

Alias: /gxp

Permission: Open to all players

Example: /givexp Steve 500



---

/retrievexp [amount]

Function: Retrieve XP from your vault.

Alias: /rxp

Permission: Open to all players

Example: /retrievexp or /retrievexp 200



---

/adminxp <resetall|set|add|remove> [player] [amount]

Function: Administrative XP controls (reset, set, add, remove).

Permission: xpvault.admin required

Example: /adminxp set Player1 1000



---

/checkxp

Function: Display current and stored XP and levels.

Alias: /cxp

Permission: Open to all players



---

🔗 PlaceholderAPI Integration

Available Placeholder:

%xpvault_saved_xp%: Displays the amount of XP saved in a player's vault.


Example Usage (Scoreboard):

lines:
  - "&aStored XP: &f%xpvault_saved_xp%"


---

💬 Message Customization

Supported Placeholders:

Placeholder	Description

%player%	Target player name
%amount%	XP amount
%current_xp%	Player's current XP
%saved_xp%	Player's saved XP
%sender%	Player who sent the XP
%action%	Action taken (set, add, remove)
%new_xp%	New XP value after the action


Example Messages (messages.yml):

plugin_enabled: "&aXPVault has been enabled!"
xp_saved: "&aYour XP has been saved!"
xp_given: "&aSuccessfully gave %amount% XP to %player%!"
xp_retrieved: "&aYou retrieved %amount% XP from your vault."
adminxp_player_success: "&a%action% %amount% XP for %player%. New saved XP: %new_xp%"
