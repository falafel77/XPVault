package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveXPCommand implements CommandExecutor {

    private final XPVault plugin;

    public SaveXPCommand(XPVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only_players_can_use_command"));
            return true;
        }

        Player player = (Player) sender;

        // التحقق من أن العالم مفعل
        if (!plugin.getWorldManager().isEnabledInWorld(player)) {
            player.sendMessage(plugin.getMessage("world_not_enabled"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("savexp_usage"));
            return true;
        }

        String arg = args[0].toLowerCase();
        long amountToSave = 0;
        boolean isLevel = false;

        long currentXP = ExperienceUtil.getTotalXP(player);

        if (arg.endsWith("l")) {
            isLevel = true;
            try {
                int levelsToSave = Integer.parseInt(arg.substring(0, arg.length() - 1));
                if (levelsToSave <= 0) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                    return true;
                }

                if (!ExperienceUtil.hasEnoughLevels(player, levelsToSave)) {
                    player.sendMessage(plugin.getMessage("not_enough_xp_saving_what_you_have")
                        .replace("%current_xp%", String.valueOf(currentXP)));
                    amountToSave = currentXP;
                } else {
                    amountToSave = ExperienceUtil.getXPForLevelDeduction(player, levelsToSave);
                }

            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " 
                    + plugin.getMessage("savexp_usage"));
                return true;
            }
        } else {
            try {
                amountToSave = Long.parseLong(arg);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " 
                    + plugin.getMessage("savexp_usage"));
                return true;
            }
        }

        if (amountToSave <= 0) {
            sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
            return true;
        }

        if (!isLevel && currentXP < amountToSave) {
            player.sendMessage(plugin.getMessage("not_enough_xp_saving_what_you_have")
                .replace("%current_xp%", String.valueOf(currentXP)));
            amountToSave = currentXP;
        }

        if (amountToSave <= 0) {
            sender.sendMessage("ليس لديك XP للحفظ!");
            return true;
        }

        long xpBeforeSaving = currentXP;
        
        ExperienceUtil.changePlayerXP(player, -amountToSave);
        
        long actualXPAfter = ExperienceUtil.getTotalXP(player);
        long actualXPSaved = xpBeforeSaving - actualXPAfter;

        long savedXP = plugin.getXpManager().getPlayerSavedXP(player);
        plugin.getXpManager().setPlayerSavedXP(player, savedXP + actualXPSaved);

        if (isLevel) {
            int levelBefore = Experience.getIntLevelFromExp(xpBeforeSaving);
            int levelAfter = player.getLevel();
            int levelsSaved = levelBefore - levelAfter;
            
            player.sendMessage(plugin.getMessage("xp_levels_saved")
                .replace("%levels%", String.valueOf(levelsSaved))
                .replace("%amount%", String.valueOf(actualXPSaved))
                .replace("%current_xp%", String.valueOf(actualXPAfter)));
        } else {
            player.sendMessage(plugin.getMessage("xp_saved")
                .replace("%amount%", String.valueOf(actualXPSaved))
                .replace("%current_xp%", String.valueOf(actualXPAfter)));
        }
        return true;
    }
}