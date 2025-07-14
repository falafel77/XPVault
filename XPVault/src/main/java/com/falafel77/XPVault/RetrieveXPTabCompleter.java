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
        long savedXP = plugin.getXpManager().getPlayerSavedXP(player);

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            if (savedXP > 0) {
                suggestions.add(String.valueOf(savedXP)); // Suggest all saved XP

                // Suggest levels if savedXP is enough for at least one level
                int currentLevel = Experience.getIntLevelFromExp(Experience.getExp(player));
                int levelsToSuggest = Experience.getIntLevelFromExp(savedXP) - currentLevel;

                if (levelsToSuggest > 0) {
                    for (int i = 1; i <= levelsToSuggest; i++) {
                        suggestions.add(i + "l"); // Suggest levels as '1l', '2l', etc.
                    }
                }

                // Add other common amounts if desired, e.g., 10, 100, etc.
                if (savedXP >= 10 && !suggestions.contains("10")) suggestions.add("10");
                if (savedXP >= 100 && !suggestions.contains("100")) suggestions.add("100");
                if (savedXP >= 1000 && !suggestions.contains("1000")) suggestions.add("1000");
            }
            return suggestions.stream()
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}