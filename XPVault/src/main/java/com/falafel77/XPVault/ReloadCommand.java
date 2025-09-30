package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final XPVault plugin;

    public ReloadCommand(XPVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("xpvault.reload")) {
            sender.sendMessage(plugin.getMessage("no_permission"));
            return true;
        }

        plugin.reloadConfig(); // Reloads the default config.yml if it exists
        plugin.createMessagesConfig(); // Re-loads messages.yml
        // Re-initialize SQLiteManager to ensure connection is fresh, though load() handles this internally
        plugin.getSqLiteManager().load();

        sender.sendMessage(plugin.getMessage("plugin_reloaded"));
        return true;
    }
}

