package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RetrieveXPCommand implements CommandExecutor {

    private final XPVault plugin;

    public RetrieveXPCommand(XPVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only_players_can_use_command"));
            return true;
        }

        Player player = (Player) sender;
        
        // Check if world is enabled
        if (!plugin.getWorldManager().isEnabledInWorld(player)) {
            player.sendMessage(plugin.getMessage("world_not_enabled"));
            return true;
        }

        long savedXP = plugin.getXpManager().getPlayerSavedXP(player);

        if (args.length == 0) {
            // Retrieve all saved XP
            if (savedXP <= 0) {
                player.sendMessage(plugin.getMessage("no_saved_xp"));
                return true;
            }

            long currentXPBefore = ExperienceUtil.getTotalXP(player);
            ExperienceUtil.changePlayerXP(player, savedXP);
            long actualXPAfter = ExperienceUtil.getTotalXP(player);
            long actualXPRetrieved = actualXPAfter - currentXPBefore;
            
            plugin.getXpManager().setPlayerSavedXP(player, savedXP - actualXPRetrieved);
            
            player.sendMessage(plugin.getMessage("all_xp_retrieved")
                .replace("%amount%", String.valueOf(actualXPRetrieved)));
            return true;

        } else if (args.length == 1) {
            String arg = args[0].toLowerCase();
            long amountToRetrieve = 0;
            boolean isLevel = false;
            
            if (arg.endsWith("l")) {
                isLevel = true;
                try {
                    int requestedLevels = Integer.parseInt(arg.substring(0, arg.length() - 1));
                    if (requestedLevels <= 0) {
                        sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                        return true;
                    }
                    
                    int currentLevel = player.getLevel();
                    int targetLevel = currentLevel + requestedLevels;
                    long currentTotalXP = Experience.getExp(player);
                    long targetTotalXP = Experience.getExpFromLevel(targetLevel);
                    amountToRetrieve = targetTotalXP - currentTotalXP;

                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " 
                        + plugin.getMessage("retrievexp_usage"));
                    return true;
                }
            } else {
                try {
                    amountToRetrieve = Long.parseLong(arg);
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " 
                        + plugin.getMessage("retrievexp_usage"));
                    return true;
                }
            }

            if (amountToRetrieve <= 0) {
                sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                return true;
            }

            if (savedXP < amountToRetrieve) {
                player.sendMessage(plugin.getMessage("not_enough_saved_xp")
                    .replace("%saved_xp%", String.valueOf(savedXP))
                    .replace("%amount%", String.valueOf(amountToRetrieve)));
                return true;
            }

            long currentXPBefore = ExperienceUtil.getTotalXP(player);
            int currentLevelBefore = player.getLevel();
            
            ExperienceUtil.changePlayerXP(player, amountToRetrieve);
            
            long actualXPAfter = ExperienceUtil.getTotalXP(player);
            int currentLevelAfter = player.getLevel();
            long actualXPRetrieved = actualXPAfter - currentXPBefore;
            
            long newSavedXP = savedXP - actualXPRetrieved;
            plugin.getXpManager().setPlayerSavedXP(player, newSavedXP);

            if (isLevel) {
                int actualLevelsGained = currentLevelAfter - currentLevelBefore;
                player.sendMessage(plugin.getMessage("partial_xp_retrieved_levels")
                    .replace("%amount%", String.valueOf(actualXPRetrieved))
                    .replace("%levels%", String.valueOf(actualLevelsGained))
                    .replace("%remaining_xp%", String.valueOf(newSavedXP)));
            } else {
                player.sendMessage(plugin.getMessage("partial_xp_retrieved")
                    .replace("%amount%", String.valueOf(actualXPRetrieved))
                    .replace("%remaining_xp%", String.valueOf(newSavedXP)));
            }
            return true;
        } else {
            sender.sendMessage(plugin.getMessage("retrievexp_usage"));
            return true;
        }
    }
}