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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only_players_can_use_command"));
            return true;
        }

        Player player = (Player) sender;

        // استخدام Experience.java للحصول على XP الحالي
        long currentXP = Experience.getExp(player);
        long storedXP = xpManager.getPlayerSavedXP(player);
        int storedLevels = Experience.getIntLevelFromExp(storedXP);

        player.sendMessage(plugin.getMessage("current_xp_message").replace("%current_xp%", String.valueOf(currentXP)));
        player.sendMessage(plugin.getMessage("stored_xp_message").replace("%stored_xp%", String.valueOf(storedXP)));
        player.sendMessage(plugin.getMessage("stored_levels_message").replace("%stored_levels%", String.valueOf(storedLevels)));

        return true;
    }
}