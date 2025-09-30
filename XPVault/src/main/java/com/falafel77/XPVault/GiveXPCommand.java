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
        if (plugin.isCooldownEnabled() && plugin.isCommandCooldownEnabled("GiveXPCommand")) {
            long now = System.currentTimeMillis();
            long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (now - last < plugin.getCooldownSeconds() * 1000) {
                long wait = (plugin.getCooldownSeconds() * 1000 - (now - last)) / 1000;
                player.sendMessage("&cPlease wait " + wait + " seconds before using this command again.");
                return true;
            }
            cooldowns.put(player.getUniqueId(), now);
        }
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
        // Check if 'l' is at the end (for levels)
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