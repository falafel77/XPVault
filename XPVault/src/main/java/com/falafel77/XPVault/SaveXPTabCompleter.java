package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaveXPTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        long currentXP = ExperienceUtil.getTotalXP(player);

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            
            // Suggest common XP amounts
            suggestions.add("10");
            suggestions.add("100");
            suggestions.add("1000");

            // Suggest levels that the player can actually save
            int currentLevel = player.getLevel();
            int maxLevelsToSuggest = Math.min(currentLevel, 10); // Suggest up to 10 levels or current level

            for (int i = 1; i <= maxLevelsToSuggest; i++) {
                // Check if player has enough levels to save
                if (ExperienceUtil.hasEnoughLevels(player, i)) {
                    suggestions.add(i + "l"); // Suggest levels as '1l', '2l', etc.
                }
            }

            return suggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}