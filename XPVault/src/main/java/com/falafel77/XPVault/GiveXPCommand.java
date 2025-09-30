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

        String arg = args[1].toLowerCase();
        long amount = 0;
        boolean isLevel = false;

        // التحقق من وجود 'l' في نهاية الرقم (للمستويات)
        if (arg.endsWith("l")) {
            isLevel = true;
            try {
                int levelsToGive = Integer.parseInt(arg.substring(0, arg.length() - 1));
                if (levelsToGive <= 0) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                    return true;
                }

                // الحساب الصحيح: حساب XP المطلوب لرفع المستويات من المستوى الحالي للهدف
                amount = ExperienceUtil.getXPForLevelAdvancement(targetPlayer, levelsToGive);
                
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
            // استخدام XPManager للحصول على XP الحالي المحفوظ
            long senderSavedXP = plugin.getXpManager().getPlayerSavedXP(playerSender);

            if (senderSavedXP < amount) {
                playerSender.sendMessage(plugin.getMessage("not_enough_xp_to_give").replace("%current_xp%", String.valueOf(senderSavedXP)));
                return true;
            }

            // خصم XP من المرسل باستخدام XPManager
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

        // إعطاء XP للهدف باستخدام XPManager
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