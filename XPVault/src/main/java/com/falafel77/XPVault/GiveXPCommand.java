package com.falafel77.XPVault;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveXPCommand implements CommandExecutor {

    private final XPVault plugin;

    public GiveXPCommand(XPVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.getMessage("givexp_usage"));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(plugin.getMessage("player_not_found"));
            return true;
        }

        // Check if both players are in enabled worlds
        if (sender instanceof Player) {
            Player senderPlayer = (Player) sender;
            if (!plugin.getWorldManager().isEnabledInWorld(senderPlayer)) {
                sender.sendMessage(plugin.getMessage("world_not_enabled"));
                return true;
            }
        }
        
        if (!plugin.getWorldManager().isEnabledInWorld(targetPlayer)) {
            sender.sendMessage(plugin.getMessage("target_world_not_enabled")
                .replace("%player%", targetPlayer.getName()));
            return true;
        }

        String arg = args[1].toLowerCase();
        long amount = 0;
        boolean isLevel = false;
        int requestedLevels = 0;

        if (arg.endsWith("l")) {
            isLevel = true;
            try {
                requestedLevels = Integer.parseInt(arg.substring(0, arg.length() - 1));
                if (requestedLevels <= 0) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                    return true;
                }
                amount = Experience.getExpFromLevel(requestedLevels);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("amount_must_be_number"));
                return true;
            }
        } else {
            try {
                amount = Long.parseLong(arg);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("amount_must_be_number"));
                return true;
            }
        }

        if (amount <= 0) {
            sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
            return true;
        }

        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            long senderSavedXP = plugin.getXpManager().getPlayerSavedXP(playerSender);

            if (senderSavedXP < amount) {
                playerSender.sendMessage(plugin.getMessage("not_enough_xp_to_give")
                    .replace("%current_xp%", String.valueOf(senderSavedXP)));
                return true;
            }

            plugin.getXpManager().removePlayerSavedXP(playerSender, amount);
            
            if (isLevel) {
                int levelsEquivalent = Experience.getIntLevelFromExp(amount);
                playerSender.sendMessage(plugin.getMessage("xp_levels_given_sender")
                    .replace("%levels%", String.valueOf(levelsEquivalent))
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%player%", targetPlayer.getName())
                    .replace("%current_xp%", String.valueOf(ExperienceUtil.getTotalXP(playerSender))));
            } else {
                playerSender.sendMessage(plugin.getMessage("xp_given_sender")
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%player%", targetPlayer.getName())
                    .replace("%current_xp%", String.valueOf(ExperienceUtil.getTotalXP(playerSender))));
            }
        } else {
            sender.sendMessage(plugin.getMessage("xp_given_console"));
        }

        plugin.getXpManager().addPlayerSavedXP(targetPlayer, amount);
        
        if (isLevel) {
            int levelsEquivalent = Experience.getIntLevelFromExp(amount);
            targetPlayer.sendMessage(plugin.getMessage("xp_levels_received")
                .replace("%levels%", String.valueOf(levelsEquivalent))
                .replace("%amount%", String.valueOf(amount))
                .replace("%sender%", sender.getName()));
        } else {
            targetPlayer.sendMessage(plugin.getMessage("xp_received")
                .replace("%amount%", String.valueOf(amount))
                .replace("%sender%", sender.getName()));
        }
        
        return true;
    }
}