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
            
            // اقتراح مقادير XP شائعة
            suggestions.add("10");
            suggestions.add("100");
            suggestions.add("1000");
            suggestions.add("10000");

            // اقتراح المستويات التي يمتلكها اللاعب
            int currentLevel = player.getLevel();
            int maxLevelsToSuggest = Math.min(currentLevel, 10);

            for (int i = 1; i <= maxLevelsToSuggest; i++) {
                if (ExperienceUtil.hasEnoughLevels(player, i)) {
                    suggestions.add(i + "l");
                }
            }

            // اقتراح XP الحالي والمستوى الحالي
            if (currentXP > 0) {
                suggestions.add(String.valueOf(currentXP));
            }
            if (currentLevel > 0) {
                suggestions.add(currentLevel + "l");
            }

            // اقتراح ديناميكي
            String input = args[0];
            List<String> dynamicSuggestions = new ArrayList<>(suggestions);
            try {
                int num = Integer.parseInt(input);
                dynamicSuggestions.clear();
                dynamicSuggestions.add(num + "l");
            } catch (NumberFormatException ignored) {}
            
            return dynamicSuggestions.stream()
                    .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}