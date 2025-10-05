package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RetrieveXPTabCompleter implements TabCompleter {

    private final XPVault plugin;

    public RetrieveXPTabCompleter(XPVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        long savedXP = plugin.getXpManager().getPlayerSavedXP(player); // Changed to long

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            
            if (savedXP > 0) {
                // اقتراح كل XP المحفوظ
                suggestions.add(String.valueOf(savedXP));

                // اقتراح مقادير شائعة إذا كانت متاحة
                if (savedXP >= 10) suggestions.add("10");
                if (savedXP >= 100) suggestions.add("100");
                if (savedXP >= 1000) suggestions.add("1000");
                if (savedXP >= 10000) suggestions.add("10000");

                // اقتراح المستويات المحفوظة
                int savedLevels = Experience.getIntLevelFromExp(savedXP);
                if (savedLevels > 0) {
                    suggestions.add(savedLevels + "l");
                    
                    // اقتراح بعض المستويات الأقل
                    for (int i = 1; i <= Math.min(savedLevels, 10); i++) {
                        suggestions.add(i + "l");
                    }
                }
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
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

