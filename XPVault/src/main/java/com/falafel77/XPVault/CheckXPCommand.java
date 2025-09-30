package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckXPCommand implements CommandExecutor {

    private final XPManager xpManager;
    private final XPVault plugin;

    public CheckXPCommand(XPManager xpManager, XPVault plugin) {
        this.xpManager = xpManager;
        this.plugin = plugin;
    }

    // Cooldown map for players
    private static final java.util.Map<java.util.UUID, Long> cooldowns = new java.util.HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only_players_can_use_command"));
            return true;
        }

        Player player = (Player) sender;
        // Check allowed worlds
        if (plugin.getAllowedWorlds() != null && !plugin.getAllowedWorlds().isEmpty() && !plugin.getAllowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage("&cThis command is not allowed in this world.");
            return true;
        }
        // Check cooldown
        if (plugin.isCooldownEnabled() && plugin.isCommandCooldownEnabled("CheckXPCommand")) {
            long now = System.currentTimeMillis();
            long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (now - last < plugin.getCooldownSeconds() * 1000) {
                long wait = (plugin.getCooldownSeconds() * 1000 - (now - last)) / 1000;
                player.sendMessage("&cPlease wait " + wait + " seconds before using this command again.");
                return true;
            }
            cooldowns.put(player.getUniqueId(), now);
        }
        // Get current and stored XP
        long currentXP = Experience.getExp(player);
        long storedXP = xpManager.getPlayerSavedXP(player);
        int storedLevels = Experience.getIntLevelFromExp(storedXP);

        player.sendMessage(plugin.getMessage("current_xp_message").replace("%current_xp%", String.valueOf(currentXP)));
        player.sendMessage(plugin.getMessage("stored_xp_message").replace("%stored_xp%", String.valueOf(storedXP)));
        player.sendMessage(plugin.getMessage("stored_levels_message").replace("%stored_levels%", String.valueOf(storedLevels)));

        return true;
    }
}