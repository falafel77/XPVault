package com.falafel77.XPVault;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminXPCommand implements CommandExecutor, TabCompleter {

    private final XPVault plugin;

    public AdminXPCommand(XPVault plugin) {
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
        if (plugin.isCooldownEnabled() && plugin.isCommandCooldownEnabled("AdminXPCommand")) {
            long now = System.currentTimeMillis();
            long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (now - last < plugin.getCooldownSeconds() * 1000) {
                long wait = (plugin.getCooldownSeconds() * 1000 - (now - last)) / 1000;
                player.sendMessage("&cPlease wait " + wait + " seconds before using this command again.");
                return true;
            }
            cooldowns.put(player.getUniqueId(), now);
        }
        if (!sender.hasPermission("xpvault.admin")) {
            sender.sendMessage(plugin.getMessage("no_permission"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessage("adminxp_usage"));
            return true;
        }
        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("resetall")) {
            if (args.length == 1) {
                plugin.getXpManager().clearAllSavedXP();
                sender.sendMessage(plugin.getMessage("adminxp_reset_all_success"));
                return true;
            } else {
                sender.sendMessage(plugin.getMessage("adminxp_reset_all_usage"));
                return true;
            }
        } else if (subCommand.equals("set") || subCommand.equals("add") || subCommand.equals("remove")) {
            if (args.length < 3) {
                sender.sendMessage(plugin.getMessage("adminxp_player_usage"));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(plugin.getMessage("player_not_found"));
                return true;
            }

            long amount;
            try {
                amount = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("amount_must_be_number"));
                return true;
            }

            long newXP = 0L;

            if (subCommand.equals("set")) {
                newXP = amount;
            } else if (subCommand.equals("add")) {
                newXP = plugin.getXpManager().getPlayerSavedXP(targetPlayer) + amount;
            } else if (subCommand.equals("remove")) {
                newXP = plugin.getXpManager().getPlayerSavedXP(targetPlayer) - amount;
            }

            if (newXP < 0) newXP = 0; // Prevent negative XP

            plugin.getXpManager().setPlayerSavedXP(targetPlayer, newXP);

            sender.sendMessage(plugin.getMessage("adminxp_player_success")
                    .replace("%player%", targetPlayer.getName())
                    .replace("%action%", subCommand)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%new_xp%", String.valueOf(newXP)));
            return true;
        } else {
            sender.sendMessage(plugin.getMessage("adminxp_usage"));
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // التحقق من صلاحيات الأدمن
        if (!sender.hasPermission("xpvault.admin")) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // الأوامر الفرعية المتاحة
            List<String> subCommands = Arrays.asList("set", "add", "remove", "resetall");
            
            // تصفية الأوامر حسب ما يكتبه اللاعب
            String input = args[0].toLowerCase();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            // إذا كان الأمر يحتاج لاسم لاعب
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("set") || subCommand.equals("add") || subCommand.equals("remove")) {
                // إضافة أسماء اللاعبين المتصلين
                String input = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 3) {
            // إذا كان الأمر يحتاج لرقم
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("set") || subCommand.equals("add") || subCommand.equals("remove")) {
                // اقتراح بعض الأرقام الشائعة
                List<String> amounts = new ArrayList<>(Arrays.asList("100", "500", "1000", "5000", "10000"));
                // إضافة اقتراحات للمستويات (مثال: 1l, 5l, 10l)
                amounts.add("1l");
                amounts.add("5l");
                amounts.add("10l");
                amounts.add("20l");
                amounts.add("30l");

                String input = args[2].toLowerCase();
                for (String amount : amounts) {
                    if (amount.startsWith(input)) {
                        completions.add(amount);
                    }
                }
            }
        }

        return completions;
    }
}